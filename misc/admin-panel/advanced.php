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
                    <h3>Reset stats</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="sr_character" name="input-target" placeholder="Character name">
                        
                        <input class="action_button" id="reset_stats" type="button" value="Apply reset">
                    </form>
                    <h3>Global stats reset</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="gsr_reason" name="input-target" placeholder="Reason">
                        
                        <input class="action_button" id="reset_stats_g" type="button" value="Apply reset">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Change position</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="cp_character" name="input-target" placeholder="Character name">
                        <input class="textbox" type="text" id="cp_position" name="input-value" placeholder="New position">
                        
                        <input class="action_button" id="change_pos" type="button" value="Apply">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Change name</h3>
                    
                    <form class="admin_form">
                        <input class="textbox" type="text" id="cn_character" name="input-target" placeholder="Character name">
                        <input class="textbox" type="text" id="cn_name" name="input-value" placeholder="New name">
                        
                        <input class="action_button" id="change_name" type="button" value="Apply">
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
