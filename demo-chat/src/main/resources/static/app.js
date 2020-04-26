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
    let socket = new SockJS('/stomp');
    let username = $("#username").val();
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/'+username, function (message) {
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
    let sendType = $('#sendMessageModal').data("sending-type");

    let message = {
        from: $("#username").val(),
        body: $("#message-text").val(),
        to: $("#recipient-name").val(),
        timestamp: Date.now()
    }

    if (sendType == 'file') {
        uploadFile(message);
    }else{
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
            let username = $("#username").val();
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
        let button = $(event.relatedTarget); // Button that triggered the modal
        let sendType = button.data('sending');
        let modal = $(this);

        modal.data("sending-type", sendType);

        if (sendType == "text") {
            $("#textFormField").show();
            $("#fileFormField").hide();

            modal.find('.modal-body textarea').val('');
        }else{
            $("#fileFormField").show();
            $("#textFormField").hide();
        }

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
    })
});