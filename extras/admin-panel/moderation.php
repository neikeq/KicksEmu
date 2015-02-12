<?php
    require_once 'includes/admin-db.php';
    require_once 'includes/authentication.php';
    
    sec_session_start();
?>

<!DOCTYPE html>

<html lang="en">

<head>
    <?php
        require "includes/head.inc.php";
    ?>
</head>

<body>
    <div class="wrapper">
        <?php
        require "includes/header.inc.php";
        ?>
        
        <?php
        $aux = login_check($db);
        if ($aux == 1) : ?>
        <div class="content">
            <?php
            require "includes/navigation.inc.php";
            require "includes/main-menu.inc.php";
            ?>
            
            <div class="page_content">              
                <div class="block_content">
                    <h3>Ban user</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="bu_user" name="input-target" placeholder="User name">
                        <input class="textbox" type="text" id="bu_expire" name="input-value" placeholder="Expire datetime">
                        <input class="textbox" type="text" id="bu_reason" name="input-value" placeholder="Reason">
                        
                        <input class="action_button" id="ban_user" type="button" value="Ban">
                    </form>
                </div>
            </div>
        </div>
        <?php
        else :
            header("Location: index.php?error=$aux");
            die();
        endif;
        ?>
    </div>
</body>

</html>
