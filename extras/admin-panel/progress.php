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
        
        <?php if (login_check($db) == true) : ?>
        <div class="content">
            <?php
                require "includes/navigation.inc.php";
                require "includes/main-menu.inc.php";
            ?>
            
            <div class="page_content">
                <div class="block_content">
                    <h3>Add points</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="ap_character" name="input-target" placeholder="Character name">
                        <input class="textbox" type="text" id="ap_value" name="input-value" placeholder="Value">
                        
                        <input class="action_button" id="add_points" type="button" value="Add">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Add experience</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="ae_character" name="input-target" placeholder="Character name">
                        <input class="textbox" type="text" id="ae_value" name="input-value" placeholder="Value">
                        
                        <input class="action_button" id="add_exp" type="button" value="Add">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Add kash</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="ak_user" name="input-target" placeholder="User name">
                        <input class="textbox" type="text" id="ak_value" name="input-value" placeholder="Value">
                        
                        <input class="action_button" id="add_kash" type="button" value="Add">
                    </form>
                </div>
            </div>
        </div>
        <?php
        else :
            header('Location: index.php');
            die();
        endif;
        ?>
    </div>
</body>

</html>
