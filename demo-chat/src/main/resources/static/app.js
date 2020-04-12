var stompClient = null;
var apiURL = "/api/v1/messages/";

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
    var socket = new SockJS('/broker');
    var username = $("#username").val();
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/queue/'+username, function (message) {
                console.log(message);
                showMessage(JSON.parse(message.body));
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
    var message = {
        from: $("#username").val(),
        body: $("#message-text").val(),
        to: $("#recipient-name").val(),
        timestamp: Date.now()
    }

    stompClient.send("/app/message", {}, JSON.stringify(message));
}

function showMessage(message) {
    var msgTemplate = "<tr>";
    var date = new Date(parseInt(message.timestamp));
    msgTemplate += "<td>"+ date.getHours() + ":" + addZero(date.getMinutes()) +"</td>";
    msgTemplate += "<td>"+ message.from +"</td>";
    msgTemplate += "<td>"+ message.body +"</td>";
    msgTemplate += "</tr>";
    $("#messages").append(msgTemplate);
}

function getOldMessages(){
    $.ajax({
        url: apiURL,
        type: "GET",
        contentType: 'application/json; charset=utf-8',
        success: function(resultData) {
            //console.log(resultData);
            var username = $("#username").val();
            resultData.forEach(function(message) {
                if (message.to == username){
                    showMessage(message);
                }
            });
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        },
        timeout: 120000,
    });
}

$(function () {
    setConnected(false);

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); getOldMessages();});
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); $('#sendMessageModal').modal('hide')});

    $('#sendMessageModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var sendType = button.data('sending');
        var modal = $(this);
        modal.find('.modal-body textarea').val('');

        if (sendType == "text") {
            $("#fileFormField").hide();
        }else{
            $("#fileFormField").show();
        }

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
    })
});