<?php
include 'password.php';
include 'database.php';

if(isset($_POST['submit'])) {
    change_password();
}

function update_pass($username, $new_password)
{
    global $db;
    
    // Hash the password
    $new_password =  hash_password($new_password);
    
    // Update the password in the database
    $stmt_pass = $db->prepare("UPDATE users SET password = ? WHERE username = ?");
    $stmt_pass->bindParam(1, $new_password);
    $stmt_pass->bindParam(2, $username);
    
    if($stmt_pass->execute()) {
        echo 'Your password has been changed successfully!';
    } else {
        echo 'Could not change password.';
    }
}

function change_password()
{
    global $db;
    
    $username = $_POST['user'];
    $password =  $_POST['pass'];
    $new_password =  $_POST['npass'];
        
    // Check if password length is valid
    $valid_pass = strlen($new_password) >= MIN_PASS_LENGTH
        && strlen($new_password) <= MAX_PASS_LENGTH;
    
    // Check if password equals with password confirmation
    $valid_pass_confirm = $new_password === $_POST['cnpass'];

    if($valid_pass && $valid_pass_confirm) {
        // Check if the account exists
        $stmt_check_pass = $db->prepare("SELECT password FROM users WHERE username = ?");
        $stmt_check_pass->bindParam(1, $username);

        if($stmt_check_pass->execute() && ($stored_password = $stmt_check_pass->fetch())) {
            // Check if password is correct
            if (validate($password, $stored_password)) {
                // Change the password
                update_pass($username, $new_password);
            } else {
                echo 'Invalid password.';
            }
        } else {
            echo 'Account does not exists.';
        }
    } else {            
        if (!$valid_pass) {
            echo 'Password size must be between '
                . MIN_PASS_LENGTH . ' and ' . MAX_PASS_LENGTH . '.<br>';
        }
        
        if (!$valid_pass_confirm) {
            echo 'Invalid password confirmation, passwords are different.<br>';
        }
    }
}
?>

<html>
    <body>
        <form action="./changepass.html">
            <input id="button" type="submit" name="back" value="Back">
        </form>
    </body>
</html>

