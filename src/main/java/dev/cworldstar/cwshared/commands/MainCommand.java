package dev.cworldstar.cwshared.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import dev.cworldstar.cwshared.Lang;
import dev.cworldstar.cwshared.utils.FormatUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

/**
 * Used internally. If you want to use this, extend {@link CommandsClass} instead. 
 * @author cworldstar
 *
 */
public class MainCommand extends ExtendedCommand implements TabExecutor, Listener {

	public MainCommand(String command, String description, String permission) {
		super(command, description, permission);
	}

	@Getter
	@Setter
	private Component incorrectSyntax = FormatUtils.mm("<gray>incorrect syntax");
	
	@Setter
	private CommandConsumer noArgsConsumer = new CommandConsumer() {
		@Override
		protected void execute(CommandSender sender, ArrayList<String> args) {
			sender.sendMessage(incorrectSyntax());
		}
	};
	
	public Component incorrectSyntax() {
		return incorrectSyntax;
	}
	
	public void registerLangCommand() {
		registerCommand("lang", new CommandConsumer() {
			@Override
			protected void execute(CommandSender player, ArrayList<String> args) {
				if(args.isEmpty()) {
					player.sendMessage(FormatUtils.mm("<red><bold>You must specify the lang to change to!"));
					return;
				}
				String lang = args.get(0);
				if(Lang.get().langExists(lang)) {
					Lang.get().change(player, lang);
				}
			}
			
			@Override
			protected List<String> getCompletions(int length) {
				return List.of("en-us");
			};
		});
	}
	
	public MainCommand(PluginCommand command) {
		this(command.getName(), command.getDescription(), command.getPermission());
        command.setExecutor(this);
        command.setTabCompleter(this);
	}

	private HashMap<String, CommandConsumer> commands = new HashMap<String, CommandConsumer>();
	
	public HashMap<String, CommandConsumer> getCommands() {
		return commands;
	}
	
	
	public void registerCommand(String id, CommandConsumer consumer) {
		commands.putIfAbsent(id, consumer);
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> strings = new ArrayList<String>();
        complete(sender, args, strings);
        List<String> returnList = new ArrayList<String>();
        String arg = args[args.length - 1].toLowerCase(Locale.ROOT);
        for (String item : strings) {
            if (item.toLowerCase(Locale.ROOT).contains(arg)) {
                returnList.add(item);
                if (returnList.size() >= 64) {
                    break;
                }
            }
            else if (item.equalsIgnoreCase(arg)) {
                return Collections.emptyList();
            }
        }
        return returnList;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		execute(sender, args);
		return true;
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if(args.length <= 0) {
			if(!commands.containsKey("")) {
				noArgsConsumer.accept(sender, new ArrayList<String>());
				return;
			}
			commands.get("").accept(sender, new ArrayList<String>(Arrays.asList(args)));
			return;
		}
	
		CommandConsumer command = commands.get(args[0]);
		if(command != null) {
			boolean permission = true;
			if(sender instanceof Player) {
				permission = command.hasPermission((Player) sender);
			}
			if(permission) {
				command.accept(sender, new ArrayList<String>(Arrays.asList(args)));
			} else {
				sender.sendMessage(FormatUtils.mm("<gray>You do not have permission to use this command.</gray>"));
			}
		} 
		else {
				sender.sendMessage(FormatUtils.mm("<red>Invalid command.</red>"));
		}
	}

	@Override
	protected void complete(CommandSender sender, String[] args, List<String> completions) {
		switch(args.length) {
			case 1:
				commands.forEach((String id, CommandConsumer command) -> {
					if(command.hidden()) {
						return;
					}
					
					switch(command.behavior()) {
						case HIDE_WITHOUT_PERMISSION:
							if(command.hasPermission((Player) sender)) {
								completions.add(id);
							}
							break;
						case LEAVE_VISIBLE:
							completions.add(id);
							break;
					}
				});
				break;
			default:
				CommandConsumer command = commands.get(args[0]);
				if(command != null) {
					boolean permission = true;
					if(sender instanceof Player) {
						permission = command.hasPermission((Player) sender);
					}
					if(permission) {
						List<String> completionMethod1 = command.getCompletions(args.length-1);
						if(completionMethod1 != null) {
							completions.addAll(completionMethod1);
						}
						if(sender instanceof Player) {
							List<String> completionMethod2 = command.getCompletions((Player) sender, args.length-1);
							if(completionMethod2 != null) {
								completions.addAll(completionMethod2);
							}
							List<String> completionMethod3 = command.getCompletions(List.of(args), args.length-1);
							if(completionMethod3 != null) {
								completions.addAll(completionMethod3);
							}
						}
					}
				}
				break;
		}
	}

	public void addAlias(PluginCommand command) {
		command.setExecutor(this);
        command.setTabCompleter(this);
	}

	@Nonnull
	public Set<Entry<String, CommandConsumer>> getSubCommands() {
		return commands.entrySet();
	}

	@Nullable
	public CommandConsumer getCommand(String command) {
		return commands.get(command);
	}

}
