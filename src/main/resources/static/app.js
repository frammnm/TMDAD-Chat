const defaultTimeout = 120000;
const apiURL = "/api/v1";
const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
const messageType = {
    Notice: 'Notice',
    Direct: 'Direct',
    Group: 'Group'
}

let stompClient = null;
let user = null;
let groups = null;
let conversations = [];

/*
let message = {
    sent_from: "",
    body: "",
    sent_to: "",
    timestamp: 1589229776178,
    type: messageType.Direct,
    received: false
}

let conversation = {
    receiver: {
        id: 0,
        name: "",
        url: ""
    },
    type: messageType.Direct,
    messages: []
}
 */

//Class Functions
function getConvName(jQueryConv = $('.active-chat')){
    return jQueryConv.find('h5').text().split('|')[0];
}

function getConvIndex(convName=''){
    let convIndex = -1;
    conversations.forEach(function (conv, index) {
        if (conv.receiver.name == convName){
            convIndex = index;
        }
    });
    return convIndex;
}

function getOwnedGroupIndex(groupName=''){
    let groupIndex = -1;
    if (!user) return groupIndex;

    user.ownedGroups.forEach(function (group, index) {
        if (group.name == groupName){
            groupIndex = index;
        }
    });
    return groupIndex;
}

//Util functions
function addZero(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}

function truncateString(str, num) {
    if (str.length <= num) {
        return str
    }
    return str.slice(0, num) + '...'
}

function getFormattedDateAndTime(date) {
    var hours = date.getHours();
    var minutes = addZero(date.getMinutes());
    var month = months[date.getMonth()];
    var day = date.getDate();

    return hours+':'+minutes+' | '+month+' '+day;
}

//Connection Functions
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#messaging-app").show();
    }
    else {
        $("#messaging-app").hide();
        stompClient = null;
        user = null;
        groups = null;
        conversations = [];

        $('#recipient-group').find('option').not(':first').remove();
        $('#owned-groups').find('option').not(':first').remove();
    }

    //Empty users and conversations
    finishProcess();
    $(".inbox-list").html('');
    $(".message-history").html('');
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
    //Clear session
    sessionStorage.clear();
    location.href='/login'
}

function connect() {
    if (!user) return;

    let socket = new SockJS('/stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        //Subscribe to groups
        handleGroups();

        //Subscribe to system alerts
        stompClient.subscribe('/topic/all', function (m) {
            //console.log(m);
            let message = JSON.parse(m.body);
            message.type = messageType.Notice;
            message.received = true;
            handleMessage(message);
        });

        //Subscribe to personal messages
        stompClient.subscribe('/queue/'+user.username, function (m) {
            //console.log(m);
            let message = JSON.parse(m.body);
            message.type = messageType.Direct;
            message.received = true;
            handleMessage(message);
        });
    });
}

function handleGroups(){
    if (!user){
        return;
    }

    let belongGroups = user.groups;
    let ownedGroups = user.ownedGroups;
    groups = belongGroups.concat(ownedGroups);
    groups.forEach(function (group) {
        handleNewGroup(group);
    });

    //TODO: Ask for groups' old messages and append
    subscribeToGroups(groups);
}

function subscribeToGroups(groups) {
    //Subscribe to Group messages
    groups.forEach(function (group) {
        stompClient.subscribe('/topic/' + group.name, function (m) {
            console.log(m);
            let message = JSON.parse(m.body);
            //Dont handle messages sent by me
            if (message.sent_from != user.username){
                message.type = messageType.Group;
                message.received = true;
                handleMessage(message);
            }
        });
    });
}

function startProcess(){
    //Disable buttons
    $('#modalAccept').prop('disabled', true);
    $('#send-message').prop('disabled', true);
    $('#file-input').prop('disabled', true);
}

function finishProcess(){
    //Enable buttons and clean message box
    $("#message-text").val('');
    $('#recipient-name').val('');
    $('#recipient-group').val('none').change();
    $('#owned-groups').val('none').change();
    $('#file-input').val(null);


    $('#modalAccept').prop('disabled', false);
    $('#send-message').prop('disabled', false);
    $('#file-input').prop('disabled', false);
}

//Message functionalities
function sendMessage(all = false) {
    //Get message body
    let msgBody = $("#message-text").val();

    if (!all && !msgBody){
        //Empty string, do nothing
        return;
    }

    //Disable buttons
    startProcess();

    let message = createMessage(user.username, getConvName(), msgBody);
    //Remove because backend doesnt support these fields
    delete message.type;
    delete message.received;

    if (all){
        message.body = $('#message-all').val();
        message.sent_to = 'all';
        //Send text message
        stompClient.send("/app/messageAll", {}, JSON.stringify(message));
        finishProcess();
        return;
    }

    let convType = messageType.Direct;
    let convIndex = -1;
    conversations.forEach(function(conv, index) {
        if (conv.receiver.name == message.sent_to) {
            convType = conv.type;
            convIndex = index;
        }
    });

    if ($('#file-input').get(0).files.length > 0) {
        uploadFileAPI(message, convIndex);
        return;
    }

    switch(convType) {
        case messageType.Group:
            //case: Group text
            stompClient.send("/app/groupMessage", {}, JSON.stringify(message));
            break;
        default:
            //case: Direct text
            stompClient.send("/app/message", {}, JSON.stringify(message));
    }

    //Update UI and object
    let template = getConversationTemplate(message, true);
    if (convIndex != -1) {
        $(".inbox-list > .chat-list-item").eq(convIndex).replaceWith(template);
        conversations[convIndex].messages.push(message);
    }
    updateUIWithMessage(message, false);
    finishProcess();
}

//Message was not sent by me
function handleMessage(message) {
    let convIndex = -1;
    let convName = message.sent_from;

    if (message.type == messageType.Notice){
        $('#systemAlert .alert-heading').text(message.sent_from);
        $('#systemAlert p').text(message.body);
        $('#systemAlert').fadeIn();
        return;
    }

    if (message.type == messageType.Group){
        convName = message.sent_to;
    }

    conversations.forEach(function (conv, index) {
        if (conv.receiver.name == convName){
            convIndex = index;
        }
    });

    let isActive = getConvName() == convName;
    let template = getConversationTemplate(message, isActive);

    if (convIndex != -1){
        $(".inbox-list > .chat-list-item").eq(convIndex).replaceWith(template);
        conversations[convIndex].messages.push(message);
    }else{
        $(".inbox-list").prepend(template);
        let newConv = createConversation(message.sent_from, message.type);
        newConv.messages.push(message);
        conversations.unshift(newConv);
    }
    updateUIWithMessage(message, true);
}

function createConversation(name='noname', type = messageType.Direct) {
    let conversation = {
        receiver: {
            id: 0,
            name: name,
            url: ""
        },
        type: type,
        messages: []
    }

    return conversation;
}

function createMessage(from='', to='', body='', type= messageType.Direct, received=false) {
    let message = {
        sent_from: from,
        body: body,
        sent_to: to,
        type: type,
        received: received,
        timestamp: new Date().getTime()
    }

    return message;
}

function getConversationTemplate(message, active=false){
    let date = new Date(parseInt(message.timestamp));
    let parsedDate = getFormattedDateAndTime(date).split(' | ');
    let activeClass = '';
    let typeIcon = 'fa-user';
    let convName = message.sent_from;

    if (message.sent_from == user.username){
        convName = message.sent_to;
    }
    if (message.type == messageType.Group){
        convName = message.sent_to;
        typeIcon = 'fa-users';
    }

    if (active) activeClass = 'active-chat';

    let convTemplate = [
        '<div class="row chat-list-item '+activeClass+'">',
            '<div class="col-md-2 chat-type" data-conv-type="'+message.type+'"><i class="fas '+typeIcon+'"></i></div>',
            '<div class="col-md-10 chat-preview">',
                '<h5>'+convName+'<span class="chat-preview-date">| '+parsedDate[1]+'</span></h5>',
                '<p>'+truncateString(message.body, 40)+'</p>',
            '</div>',
        '</div>'
    ];
    return convTemplate.join('');
}

function getMessageTemplate(message){
    let date = new Date(parseInt(message.timestamp));
    let parsedDate = getFormattedDateAndTime(date);
    let colWrapper = 'outgoing-message';
    let innerWrapper = 'sent-message';
    let messageUsername = '';

    if (message.received){
        colWrapper = 'incoming-message';
        innerWrapper = 'received-message';
        if (message.type == messageType.Group){
            messageUsername = '@'+message.sent_from;
        }
    }

    //Check if is a file message and change body to link

    let msgTemplate = [
        '<div class="col-md-12 '+colWrapper+'">',
            '<div class="'+innerWrapper+'">',
                '<div class="message-wrapper">',
                    '<p>'+message.body+'</p>',
                    '<span class="message-time-date">'+parsedDate+'</span> <span class="group-message-username">'+messageUsername+'</span>',
                '</div>',
            '</div>',
        '</div>'
    ];
    return msgTemplate.join('');
}

function updateUIWithMessage(message, isReceived=false){
    let activeName = getConvName();
    let isGroup = message.sent_to != user.username;

    if (isReceived){
        if ((message.sent_from == activeName && !isGroup)|| message.sent_to == activeName){
            $('.message-history').append(getMessageTemplate(message));
        }
    }else{
        if (message.sent_to == activeName){
            $('.message-history').append(getMessageTemplate(message));
        }
    }
}

function refreshMessages(){
    let activeName = getConvName();
    for (let conv of conversations) {
        if (conv.receiver.name == activeName) {
            $('.message-history').html('');
            conv.messages.forEach(function (message) {
                $('.message-history').append(getMessageTemplate(message));
            })
            break;
        }
    }
}

function handleNewGroup(group, active=false){
    //Create Message
    let message = createMessage('', group.name, '', messageType.Group);
    let newConv = createConversation(group.name, message.type);
    conversations.unshift(newConv);
    let template = getConversationTemplate(message, active);
    $(".inbox-list").prepend(template);
    $("#recipient-group").append($("<option />").val(group.name).text(group.name));
    //If is owned, add to dropdown
    if (getOwnedGroupIndex(group.name) >= 0 ){
        $("#owned-groups").append($("<option />").val(group.name).text(group.name));
    }
}

function getUserAPI(username){
    if (!username) return;

    $.ajax({
        url: apiURL+"/users/byUsername/"+username,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        success: function(resultData) {
            user = resultData;
            console.log(user);
            handleGroups();
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        },
        timeout: defaultTimeout,
    });
}

function uploadFileAPI(message, convIndex=-1) {
    let formData = new FormData();
    formData.append('message',
        new Blob([JSON.stringify(message)],
            {
                type: "application/json"
            }
        )
    );
    // Attach file
    formData.append('file', $('#file-input')[0].files[0]);

    $.ajax({
        url: '/files/upload',
        data: formData,
        type: 'POST',
        //cache: false,
        //timeout: 600000,
        contentType: false, // NEEDED, DON'T OMIT THIS (requires jQuery 1.6+)
        processData: false, // NEEDED, DON'T OMIT THIS
        success: function(path) {
            finishProcess();
            let pathArray = path.split('/');
            let fileName = pathArray[pathArray.length - 1];
            message.body += '<br> <a href="' + path +'">'+fileName+'</a>';
            if (convIndex != -1){
                conversations[convIndex].messages.push(message);
            }
            updateUIWithMessage(message, false);
            console.log(path);
        },
        error: function(err) {
            finishProcess();
            console.log(err.responseJSON);
        }
    });
}

function createGroupAPI(groupName = ''){
    startProcess();
    //Create Message
    let group = {
        name: groupName,
        owner: user
    }

    $.ajax({
        url: apiURL+'/groups/create',
        data: JSON.stringify(group),
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        success: function(res) {
            console.log(res);
            user.ownedGroups.push(res);
            $('.active-chat').removeClass('active-chat');
            handleNewGroup(group);
            refreshMessages();
            subscribeToGroups([group]);
            finishProcess();
        },
        error: function(err) {
            console.log(err.responseJSON);
            finishProcess();
        }
    });
}

function handleModalAccept(){
    let actionType = $('#actionModal').data("action-type");
    let convName = $('#recipient-name').val();
    let convIndex = -1;

    switch(actionType) {
        case 'group-new':
            if (!convName) return;

            convIndex = getConvIndex(convName);
            if (convIndex == -1) {
                createGroupAPI(convName);
            }
            break;
        case 'group-addPerson':
            //Validate field not empty, and group is selected
            let group = $('#owned-groups').val();
            if (!convName || group == 'none') return;

            alert('añadir persona');
            break;
        case "send-all":
            sendMessage(true);
            break;
        default:
            //case: conv-new
            if (!convName) return;

            convIndex = getConvIndex(convName);
            if (convIndex == -1){
                //Create Conversation
                let message = createMessage(user.username, convName);
                let newConv = createConversation(convName, message.type);
                conversations.unshift(newConv);
                let template = getConversationTemplate(message, true);
                $('.active-chat').removeClass('active-chat');
                $(".inbox-list").prepend(template);
                refreshMessages();
                finishProcess();
            }
    }
}

function initApp(){
    setConnected(false);
    //Get user
    user = JSON.parse(sessionStorage.getItem('session-user'));
    console.log(user);
    if (user){
        connect();
    }
}

$(function () {
    initApp();
    $( "#logout" ).click(function() { disconnect(); });

    //This is to be able to hide alerts
    $("[data-hide]").on("click", function(){
        $("#" + $(this).attr("data-hide")).hide();
    });

    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $('body').on('click', '#send-message', function() { sendMessage();});

    //Filter conversations on searchbar keyup
    $('body').on("keyup", '#convSearchbar', function() {
        var value = $(this).val().toLowerCase();
        $(".inbox-list > div").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });

    //Change active conversation
    $('body').on('click', '.inbox-list > div.chat-list-item', function() {
        let conv = $(this);
        let convName = getConvName(conv);
        let activeName = getConvName();

        if (convName != activeName){
            $('.active-chat').removeClass('active-chat');
            conv.addClass('active-chat');
            refreshMessages();
        }
    });

    //Handle action modal
    $('#actionModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget); // Button that triggered the modal
        let sendType = button.data('sending');
        let modal = $(this);

        modal.data("action-type", sendType);

        switch(sendType) {
            case "group-new":
                $("#modalTitle").text('Nuevo Grupo');
                $("#recipientField > label").text('Nombre del grupo:');
                $("#recipientField").show();
                $("#textMessageField").hide();
                $("#groupListField").hide();
                $("#ownGroupListField").hide();
                break;
            case "group-addPerson":
                $("#modalTitle").text('Añadir persona a un grupo');
                $("#recipientField > label").text('Usuario:');
                $("#recipientField").show();
                $("#textMessageField").hide();
                $("#groupListField").hide();
                $("#ownGroupListField").show();

                modal.find('#textRecipientField').val('');
                break;
            case "send-all":
                $("#modalTitle").text('Nuevo Mensaje de Sistema');
                $("#recipientField").hide();
                $("#textMessageField").show();
                $("#groupListField").hide();
                $("#ownGroupListField").hide();

                modal.find('.modal-body textarea').val('');
                break;
            default:
                //case: conv-new
                $("#modalTitle").text('Nueva Conversación');
                $("#recipientField > label").text('Usuario:');
                $("#recipientField").show();
                $("#textMessageField").hide();
                $("#groupListField").hide();
                $("#ownGroupListField").hide();

                modal.find('#textRecipientField').val('');
        }
    });
    $( "#modalAccept" ).click(function() { handleModalAccept(); $('#actionModal').modal('hide');});
});