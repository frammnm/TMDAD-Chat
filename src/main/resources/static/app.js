var stompClient = null;
var apiURL = "/api/v1/";
var user = null;
var defaultTimeout = 120000;

//START FUNCTIONS

function addZero(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
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

        //Subscribe to personal messages
        stompClient.subscribe('/queue/'+username, function (m) {
                console.log(m);
                let message = JSON.parse(m.body);
                message.type = 'Directo';
                showMessage(message);
        });

        //Subscribe to system alerts
        stompClient.subscribe('/topic/all', function (m) {
            console.log(m);
            let message = JSON.parse(m.body);
            message.type = 'Aviso';
            showMessage(message);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
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

function showMessage(message) {
    let msgTemplate = "<tr>";
    let date = new Date(parseInt(message.timestamp));
    msgTemplate += "<td>"+ message.type +"</td>";
    msgTemplate += "<td>"+ date.getHours() + ":" + addZero(date.getMinutes()) +"</td>";
    msgTemplate += "<td>"+ message.sent_from +"</td>";
    msgTemplate += "<td>"+ message.sent_to +"</td>";
    msgTemplate += "<td>"+ message.body +"</td>";
    msgTemplate += "</tr>";
    $("#messages").append(msgTemplate);
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

function handleGroups(){
    if (user == null){
        return;
    }

    let belongGroups = user.groups;
    let ownedGroups = user.ownedGroups;
    let groups = belongGroups.concat(ownedGroups);
    console.log(groups);

    //Fill dropdown
    let dropdown = $("#recipient-group");
    groups.forEach(function (group) {
        dropdown.append($("<option />").val(group.name).text(group.name));
    });

    subscribeToGroups(groups);
}

function subscribeToGroups(groups) {
    //Subscribe to Group messages
    groups.forEach(function (group) {
        stompClient.subscribe('/topic/' + group.name, function (m) {
            console.log(m);
            let message = JSON.parse(m.body);
            message.type = 'Grupo';
            showMessage(message);
        });
    });
}

function getOldMessages(){
    $.ajax({
        url: apiURL+"/messages/",
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        success: function(resultData) {
            //console.log(resultData);
            let username = $("#username").val();
            resultData.forEach(function(message) {
                if (message.sent_to == username){
                    message.sent_to = 'Yo';
                    message.type = 'Directo'
                    showMessage(message);
                }
            });
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        },
        timeout: defaultTimeout,
    });
}

$(function () {
    setConnected(false);

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect();});
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); $('#sendMessageModal').modal('hide')});

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

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
    })
});