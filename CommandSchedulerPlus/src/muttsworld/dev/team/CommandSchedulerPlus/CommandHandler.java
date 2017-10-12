package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CommandHandler implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

        }
/*        StringBuilder builder = new StringBuilder();
        for(String s : args) {
            builder.append(s);
        }
        String str = builder.toString();*/
        String str = String.join(" ", args);
        
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();;
        Bukkit.dispatchCommand(console, str);
        System.out.print(str);
        
        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
