ALTER TABLE characters
MODIFY `item_head` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_glasses` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_shirts` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_pants` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_glove` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_shoes` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_socks` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_wrist` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_arm` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_knee` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_ear` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_neck` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_mask` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_muffler` int(10) NOT NULL DEFAULT '-1',
MODIFY `item_package` int(10) NOT NULL DEFAULT '-1';

UPDATE characters
SET item_head=-1,item_glasses=-1,item_shirts=-1,item_pants=-1,item_glove=-1,
item_shoes=-1,item_socks=-1,item_wrist=-1,item_arm=-1,item_knee=-1,
item_ear=-1,item_neck=-1,item_mask=-1,item_muffler=-1,item_package=-1;