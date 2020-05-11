const defaultTimeout = 120000;
const apiURL = "/api/v1/";
const messageType = ['Notice', 'Direct', 'Group'];
const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

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
    type: messageType[1],
    received: false
}

let conversation = {
    receiver: {
        id: 0,
        name: "",
        url: ""
    },
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
    var minutes = date.getMinutes();
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
            message.type = messageType[0];
            message.received = true;
            handleMessage(message);
        });

        //Subscribe to personal messages
        stompClient.subscribe('/queue/'+username, function (m) {
            //console.log(m);
            let message = JSON.parse(m.body);
            message.type = messageType[1];
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
    console.log(groups);

    //Create Message
    let message = createMessage();
    message.type = messageType[2];
    message.received = false;

    //Fill dropdown
    //let dropdown = $("#recipient-group");
    groups.forEach(function (group) {
        let newConv = createConversation(group.name);
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
            message.type = messageType[2];
            message.received = true;
            handleMessage(message);
        });
    });
}

//Message functionalities
function sendMessage(isFile = false) {
    let sendType = $('#sendMessageModal').data("sending-type");

    let message = {
        sent_from: $("#username").val(),
        body: $("#message-text").val(),
        sent_to: $("#recipient-name").val(),
        timestamp: Date.now()
    }

    switch(sendType) {
        case 'file':
            uploadFile(message);
            break;
        case 'textAll':
            message.sent_to = 'all';
            //Send text message
            stompClient.send("/app/messageAll", {}, JSON.stringify(message));
            break;
        case "textGroup":
            let sendTo = $("#recipient-group").val();
            if (sendTo == 'none'){
                return;
            }
            message.sent_to = sendTo;
            stompClient.send("/app/groupMessage", {}, JSON.stringify(message));
            break;
        default:
            //case: text
            //Send text message
            stompClient.send("/app/message", {}, JSON.stringify(message));
    }
}

function handleMessage(message) {
    let convIndex = -1;
    let convName = message.sent_from;

    if (message.type == messageType[2]){
        convName = message.sent_to;
    }

    conversations.forEach(function (conv, index) {
        if (conv.receiver.name == convName){
            convIndex = index;
        }
    });

    let template = getConversationTemplate(message);

    if (convIndex != -1){
        $(".inbox-list > .chat-list-item").eq(convIndex).replaceWith(template);
        conversations[convIndex].messages.push(message);
    }else{
        $(".inbox-list").prepend(template);
        let newConv = createConversation(message.sent_from);
        newConv.messages.push(message);
        conversations.unshift(newConv);
    }
    updateUIWithMessage(message);
}

function createConversation(name='noname') {
    let conversation = {
        receiver: {
            id: 0,
            name: name,
            url: ""
        },
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

function getConversationTemplate(message){
    let date = new Date(parseInt(message.timestamp));
    let parsedDate = getFormattedDateAndTime(date).split(' | ');
    let typeIcon = 'fa-user';
    let convName = message.sent_from;

    if (message.type == messageType[2]){
        convName = message.sent_to;
        typeIcon = 'fa-users';
    }

    let convTemplate = [
        '<div class="row chat-list-item">',
            '<div class="col-md-2 chat-type"><i class="fas '+typeIcon+'"></i></div>',
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

    if (message.received){
        colWrapper = 'incoming-message';
        innerWrapper = 'received-message';
    }

    //Check if is a file message and change body to link

    let msgTemplate = [
        '<div class="col-md-12 '+colWrapper+'">',
            '<div class="'+innerWrapper+'">',
                '<div class="message-wrapper">',
                    '<p>'+message.body+'</p>',
                    '<span class="message-time-date">'+parsedDate+'</span>',
                '</div>',
            '</div>',
        '</div>'
    ];
    return msgTemplate.join('');
}

function updateUIWithMessage(message){
    let activeName = getConvName();
    if (message.sent_from == activeName || message.sent_to == activeName){
        $('.message-history').append(getMessageTemplate(message));
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

function uploadFile(message) {
    let formData = new FormData();
    formData.append('message',
        new Blob([JSON.stringify(message)],
            {
                type: "application/json"
            }
        )
    );
    // Attach file
    formData.append('file', $('#uploadFile')[0].files[0]);

    $.ajax({
        url: '/files/upload',
        data: formData,
        type: 'POST',
        //cache: false,
        //timeout: 600000,
        contentType: false, // NEEDED, DON'T OMIT THIS (requires jQuery 1.6+)
        processData: false, // NEEDED, DON'T OMIT THIS
        success: function(msg) {
            console.log(msg);
        },
        error: function(err) {
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
    $('body').on('click', '#send-file', function() { sendMessage(true);});
    $('body').on('click', '#send-message', function() { sendMessage(false);});

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

    $('#sendMessageModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget); // Button that triggered the modal
        let sendType = button.data('sending');
        let modal = $(this);

        modal.data("sending-type", sendType);

        switch(sendType) {
            case "file":
                $("#fileFormField").show();
                $("#textRecipientField").show();
                $("#textFormField").hide();
                $("#groupRecipientField").hide();
                break;
            case "textAll":
                $("#textFormField").show();
                $("#textRecipientField").hide();
                $("#fileFormField").hide();
                $("#groupRecipientField").hide();
                modal.find('.modal-body textarea').val('');
                break;
            case "textGroup":
                $("#groupRecipientField").show();
                $("#textFormField").show();
                $("#textRecipientField").hide();
                $("#fileFormField").hide();

                modal.find('.modal-body textarea').val('');
                break;
            default:
                //case: text
                $("#textFormField").show();
                $("#textRecipientField").show();
                $("#fileFormField").hide();
                $("#groupRecipientField").hide();

                modal.find('.modal-body textarea').val('');
        }
    })
});