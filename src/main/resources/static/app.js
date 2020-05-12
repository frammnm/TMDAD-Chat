const defaultTimeout = 120000;
const apiURL = "/api/v1/";
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
    }

    //Empty users and conversations
    $(".inbox-list").html('');
    $(".message-history").html('');
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function connect() {
    let socket = new SockJS('/stomp');
    let username = $("#username").val();
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        //Get user
        getUser(username);

        //Subscribe to system alerts
        stompClient.subscribe('/topic/all', function (m) {
            //console.log(m);
            let message = JSON.parse(m.body);
            message.type = messageType.Notice;
            message.received = true;
            handleMessage(message);
        });

        //Subscribe to personal messages
        stompClient.subscribe('/queue/'+username, function (m) {
            //console.log(m);
            let message = JSON.parse(m.body);
            message.type = messageType.Direct;
            message.received = true;
            handleMessage(message);
        });
    });
}

function handleGroups(){
    if (user == null){
        return;
    }

    let belongGroups = user.groups;
    let ownedGroups = user.ownedGroups;
    groups = belongGroups.concat(ownedGroups);
    //console.log(groups);

    //Create Message
    let message = createMessage();
    message.type = messageType.Group;
    message.received = false;

    //Fill dropdown
    //let dropdown = $("#recipient-group");
    groups.forEach(function (group) {
        let newConv = createConversation(group.name, message.type);
        conversations.unshift(newConv);
        //const clonedMessage = Object.assign({}, message);
        message.sent_to = group.name;
        let template = getConversationTemplate(message);
        $(".inbox-list").prepend(template);
    //    dropdown.append($("<option />").val(group.name).text(group.name));
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
    $('#send-message').prop('disabled', true);
    $('#file-input').prop('disabled', true);
}

function finishProcess(){
    //Enable buttons and clean message box
    $("#message-text").val('');
    $('#file-input').val(null);

    $('#send-message').prop('disabled', false);
    $('#file-input').prop('disabled', false);
}

//Message functionalities
function sendMessage(all = false) {
    //Disable buttons
    startProcess();

    let message = {
        sent_from: user.username,
        body: $("#message-text").val(),
        sent_to: getConvName(),
        timestamp: Date.now()
    }

    if (all){
        message.sent_to = 'all';
        //Send text message
        stompClient.send("/app/messageAll", {}, JSON.stringify(message));
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
        uploadFile(message, convIndex);
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

function createMessage(from='', to='', body='') {
    let message = {
        sent_from: from,
        body: body,
        sent_to: to,
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

function getUser(username){
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

function uploadFile(message, convIndex=-1) {
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

function getConvName(jQueryConv = $('.active-chat')){
    return jQueryConv.find('h5').text().split('|')[0];
}

$(function () {
    setConnected(false);

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect();});
    $( "#disconnect" ).click(function() { disconnect(); });
    $('body').on('click', '#send-message', function() { sendMessage();});

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
});