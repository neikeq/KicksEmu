<?php
include 'password.php';
include 'database.php';

if(isset($_POST['submit'])) {
    sign_up();
}

function add_user_to_db($username, $password, $email)
{
    global $db;
    
    // Hash the password
    $password =  hash_password($password);
    
    // Add the new user to the database
    $stmt_create = $db->prepare('INSERT INTO users (username, password, email) VALUES (?, ?, ?)');
    $stmt_create->bindParam(1, $username);
    $stmt_create->bindParam(2, $password);
    $stmt_create->bindParam(3, $email);
    
    if($stmt_create->execute()) {
        echo 'Your account have been successfully registered!';
    } else {
        echo 'Could not create the account.';
    }
}

function sign_up()
{
    global $db;
    
    $username = $_POST['user'];
    $password =  $_POST['pass'];
    $email = $_POST['email'];
    
    // Check if username length is valid
    $valid_user = strlen($username) >= MIN_USER_LENGTH
        && strlen($username) <= MAX_USER_LENGTH;
        
    // Check if password length is valid
    $valid_pass = strlen($password) >= MIN_PASS_LENGTH
        && strlen($password) <= MAX_PASS_LENGTH;
    
    // Check if password equals with password confirmation
    $valid_pass_confirm = $password === $_POST['cpass'];
        
    // Check if email length is valid
    $valid_email = strlen($email) >= 3;

    if($valid_user && $valid_pass && $valid_email && $valid_pass_confirm) {
        // Check if there is an account with this username
        $stmt_check_user = $db->prepare("SELECT 1 FROM users WHERE username = ?");
        $stmt_check_user->bindParam(1, $username);
        
        // Check if there is an account with this email
        $stmt_check_email = $db->prepare("SELECT 1 FROM users WHERE email = ?");
        $stmt_check_email->bindParam(1, $email);

        if($stmt_check_user->execute() && $stmt_check_user->fetch()) {
            echo 'An account with that username already exists.';
        } else if($stmt_check_email->execute() && $stmt_check_email->fetch()) {
            echo 'An account with that email address already exists.';
        } else {
            // Create the account
            add_user_to_db($username, $password, $email);
        }
    } else {
        if (!$valid_user) {
            echo 'Username size must be between '
                . MIN_USER_LENGTH . ' and ' . MAX_USER_LENGTH . '.<br>';
        }
        
        if (!$valid_pass) {
            echo 'Password size must be between '
                . MIN_PASS_LENGTH . ' and ' . MAX_PASS_LENGTH . '.<br>';
        }
        
        if (!$valid_pass_confirm) {
            echo 'Invalid password confirmation, passwords are different.<br>';
        }
        
        if (!$valid_email) {
            echo 'Invalid email address.<br>';
        }
    }
}
?>

<html>
    <body>
        <form action="./signup.html">
            <input id="button" type="submit" name="back" value="Back">
        </form>
    </body>
</html>

