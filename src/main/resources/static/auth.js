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
        url: apiURL+'/users/signup',
        data: JSON.stringify(user),
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        success: function(res) {
            sessionStorage.setItem('sesionJTW', res);
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
})