<?php
    require_once 'includes/authentication.php';
    require_once 'includes/admin-db.php';
    require_once 'includes/hashing.inc.php';
    
    sec_session_start();
?>

<!DOCTYPE html>

<html lang="en">

<head>
    <?php
        require "includes/head.inc.php";
    ?>
    
    <style>
        div.page_content {
            margin-left: 0;
        }
    </style>
</head>

<body>
    <div class="wrapper">
        <?php
            require "includes/header.inc.php";
        ?>
        
        <div class="content">            
            <div class="page_content">
                <div class="block_content" id="login_content">
                    <h3>Login</h3>
                    
                     <form class="admin_form" action="includes/login.php" method="post">
                        <input class="textbox" type="text" id="username"
                               name="username" placeholder="Username">
                        <input class="textbox" type="password" size="25"
                               id="password" name="password" placeholder="Password">
                        
                        <input class="action_button" id="login" type="submit" value="Login"
                               onclick="password_hash(this.form, this.form.password);">
                    </form>
                </div>
            </div>
        
        
            <?php
                if (isset($_GET['error'])) {
                    echo '<p class="error">Logging error!</p>';
                }
            ?>
        </div>
        
        <?php
            if (login_check($db) == 1) {
                header('Location: moderation.php');
                die();
            }
        ?>
    </div>
</body>

</html>
