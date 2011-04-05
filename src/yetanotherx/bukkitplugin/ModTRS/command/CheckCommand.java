package yetanotherx.bukkitplugin.ModTRS.command;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import yetanotherx.bukkitplugin.ModTRS.ModTRS;
import yetanotherx.bukkitplugin.ModTRS.ModTRSMessage;
import yetanotherx.bukkitplugin.ModTRS.ModTRSPermissions;
import yetanotherx.bukkitplugin.ModTRS.sql.ModTRSRequest;
import yetanotherx.bukkitplugin.ModTRS.sql.ModTRSRequestTable;
import yetanotherx.bukkitplugin.ModTRS.sql.ModTRSUserTable;

public class CheckCommand implements CommandExecutor {

    public CheckCommand(ModTRS parent) {
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	
	int page = 1;
	String type = "open";
	
	for( String arg : args ) {
	    
	    if( arg.substring(0, 2).equals("p:") ) {
		page = Integer.parseInt( arg.substring(2) );
	    }
	    if( arg.substring(0, 2).equals("t:") ) {
		type = arg.substring(2);
	    }
	}
	
	Player player = (Player) sender;

	if( !ModTRSPermissions.has(player, "modtrs.command.check") ) {
	    player.sendMessage(ModTRSMessage.noPermission);
	    return true;
	}

	try {

	    ArrayList<ModTRSRequest> requests;
	    requests = ModTRSRequestTable.getOpenRequests(type);

	    String ucfirst = type.toUpperCase().substring(0, 1) + type.substring(1);

	    player.sendMessage( ModTRSMessage.parse(ModTRSMessage.listIntro, new Object[] { ucfirst, requests.size() } ) );

	    int count = 0;
	    if( requests.size() == 0 ) {
		player.sendMessage( ModTRSMessage.noRequests );
	    }
	    for( ModTRSRequest request : requests ) {
		if( count < ( page * 5 ) - 5 ) {
		    count++;
		    continue;
		}
		if( count >= ( page * 5 ) ) {
		    player.sendMessage( ModTRSMessage.parse(ModTRSMessage.tooMany, new Object[] { ( requests.size() - 5 ) } ) );
		    break;
		}

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(CommandHandler.TIMEDATE_FORMAT);

		calendar.setTimeInMillis( request.getTimestamp() );

		String substring = request.getText();

		if( substring.length() >= 25 ) {
		    substring = substring.substring(0, 25) + "...";
		}

		if( request.getStatus() == 1 ) {
		    Object[] params = new Object[] {
			    request.getId(),
			    sdf.format(calendar.getTime()),
			    ModTRSUserTable.getUserFromId(request.getUserId()).getName(),
			    substring,
			    ModTRSUserTable.getUserFromId(request.getModId()).getName()
		    };
		    player.sendMessage( ModTRSMessage.parse(ModTRSMessage.listItemClaimed, params ) );
		}
		else {
		    Object[] params = new Object[] {
			    request.getId(),
			    sdf.format(calendar.getTime()),
			    ModTRSUserTable.getUserFromId(request.getUserId()).getName(),
			    substring
		    };
		    player.sendMessage( ModTRSMessage.parse(ModTRSMessage.listItem, params ) );
		}
		
		count++;
	    }


	}
	catch( SQLException e ) {
	    e.printStackTrace();
	    player.sendMessage( ModTRSMessage.internalError );
	}

	return true;

    }

}