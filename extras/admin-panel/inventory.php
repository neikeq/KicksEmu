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
                    <h3>Add Item</h3>
                    
                     <form class="admin_form">
                        <input class="textbox" type="text" id="ai_character" name="input-target" placeholder="Character name">
                        
                        <table class="admin_table">
                            <tr>
                                <th class="data_col">Item Id</th>
                                <th class="data_col">Expiration</th>
                                <th class="data_col">Bonus One</th>
                            </tr>
                            <tr class="datarow">
                                <td><input class="data" type="text" id="ai_id" name="id" value=""></td>
                                <td><input class="data" type="text" id="ai_expiration" name="expiration" value=""></td>
                                <td><input class="data" type="text" id="ai_bonusone" name="bonusone" value=""></td>
                            </tr>
                        </table>
                        
                        <table class="admin_table">
                            <tr>
                                <th class="data_col">Bonus Two</th>
                                <th class="data_col">Usages</th>
                                <th class="data_col">Timestamp</th>
                            </tr>
                            <tr class="datarow">
                                <td><input class="data" type="text" id="ai_bonustwo" name="bonustwo" value=""></td>
                                <td><input class="data" type="text" id="ai_usages" name="usages" value=""></td>
                                <td><input class="data" type="text" id="ai_expire" name="expire_ts" value=""></td>
                            </tr>
                        </table>
                        
                        <input class="action_button" id="add_item" type="button" value="Apply">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Add Skill</h3>
                    
                     <form class="admin_form">
                        <input class="textbox" type="text" id="as_character" name="input-target" placeholder="Character name">
                        
                        <table class="admin_table">
                            <tr>
                                <th class="data_col">Skill Id</th>
                                <th class="data_col">Expiration</th>
                                <th class="data_col">Timestamp</th>
                            </tr>
                            <tr class="datarow">
                                <td><input class="data" type="text" id="as_id" name="id" value=""></td>
                                <td><input class="data" type="text" id="as_expiration" name="expiration" value=""></td>
                                <td><input class="data" type="text" id="as_expire" name="expire_ts" value=""></td>
                            </tr>
                        </table>
                        
                        <input class="action_button" id="add_skill" type="button" value="Apply">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Add Celebration</h3>
                    
                     <form class="admin_form">
                        <input class="textbox" type="text" id="ac_character" name="input-target" placeholder="Character name">
                        
                        <table class="admin_table">
                            <tr>
                                <th class="data_col">Cere Id</th>
                                <th class="data_col">Expiration</th>
                                <th class="data_col">Timestamp</th>
                            </tr>
                            <tr class="datarow">
                                <td><input class="data" type="text" id="ac_id" name="id" value=""></td>
                                <td><input class="data" type="text" id="ac_expiration" name="expiration" value=""></td>
                                <td><input class="data" type="text" id="ac_expire" name="expire_ts" value=""></td>
                            </tr>
                        </table>
                        
                        <input class="action_button" id="add_cere" type="button" value="Apply">
                    </form>
                </div>
                
                <div class="block_content">
                    <h3>Add Training</h3>
                    
                     <form class="admin_form">
                        <input class="textbox" type="text" id="at_character" name="input-target" placeholder="Character name">
                        
                        <table class="admin_table">
                            <tr>
                                <th class="data_col">Training Id</th>
                            </tr>
                            <tr class="datarow">
                                <td><input class="data" type="text" id="at_id" name="id" value=""></td>
                            </tr>
                        </table>
                        
                        <input class="action_button" id="add_learn" type="button" value="Apply">
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
