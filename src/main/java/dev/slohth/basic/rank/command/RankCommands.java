package dev.slohth.basic.rank.command;

import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.user.User;
import dev.slohth.basic.utils.framework.command.Args;
import dev.slohth.basic.utils.framework.command.Command;

public class RankCommands {

    private final Basic core;

    public RankCommands(Basic core) { this.core = core; core.getFramework().registerCommands(this); }

    @Command(name = "setrank", inGameOnly = true)
    public void setrankCommand(Args args) {
        if (args.getArgs().length == 1 && core.getRankManager().getRank(args.getArgs(0)) != null) {
            Rank rank = core.getRankManager().getRank(args.getArgs(0));
            User user = core.getUserManager().getUser(args.getPlayer().getUniqueId());
            user.getRanks().clear();
            user.getRanks().add(core.getRankManager().getDefault());
            user.getRanks().add(rank);
            user.setDisplay(rank);
            user.recalculatePermissions();
            user.sendMessage("&bYou are now " + rank.getDisplay());
        }
    }

    @Command(name = "rank", permission = "basic.admin.rank", inGameOnly = true)
    public void setRankCommand(Args args) {

    }

    @Command(name = "hide", permission = "basic.hide", inGameOnly = true)
    public void hideCommand(Args args) {
        User user = core.getUserManager().getUser(args.getPlayer().getUniqueId());
        Rank rank = user.getDisplay();
        if (rank.equals(core.getRankManager().getDefault()) && !user.getPrimaryRank().equals(rank)) {
            user.setDisplay(user.getPrimaryRank());
            user.sendMessage("&bYou are no longer hidden");
        } else {
            user.setDisplay(core.getRankManager().getDefault());
            user.sendMessage("&bYou are now hidden");
        }
    }

}
