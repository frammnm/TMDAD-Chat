const apiURL = "/api/v1";

function validate() {
    return ($('#username').val() && $('#password').val())
}

function login(){
    if (!validate()){
        $('#systemAlert .alert-heading').text('Incorrect Login');
        $('#systemAlert p').text('Username and password are required');
        $('#systemAlert').fadeIn();
        return;
    }

    //Create User
    let user = {
        username: $('#username').val(),
        password: $('#password').val()
    }

    $.ajax({
        url: apiURL+'/users/signin',
        data: JSON.stringify(user),
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        success: function(res) {
            console.log(res);
            sessionStorage.setItem('session-token', res.jwt);
            sessionStorage.setItem('session-user', JSON.stringify(res.user));
            location.href='/'
        },
        error: function(err) {
            console.log(err.responseJSON);
            $('#systemAlert .alert-heading').text('Incorrect Login');
            $('#systemAlert p').text('Username or password incorrect');
            $('#systemAlert').fadeIn();
        }
    });
}

function register(){
    if (!validate()){
        $('#systemAlert .alert-heading').text('Incorrect Login');
        $('#systemAlert p').text('Username and password are required');
        $('#systemAlert').fadeIn();
        return;
    }

    //Create User
    let user = {
        username: $('#username').val(),
        password: $('#password').val()
    }

    $.ajax({
        url: apiURL+'/users/signup',
        data: JSON.stringify(user),
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        success: function(res) {
            console.log(res);
            login();
        },
        error: function(err) {
            console.log(err.responseJSON);
            $('#systemAlert .alert-heading').text('Incorrect Login');
            $('#systemAlert p').text('Username or password incorrect');
            $('#systemAlert').fadeIn();
        }
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $('body').on('click', '#send-button', function() { login();});
    $('body').on('click', '#register-button', function() { register();});

    //This is to be able to hide alerts
    $("[data-hide]").on("click", function(){
        $("#" + $(this).attr("data-hide")).hide();
    });
})