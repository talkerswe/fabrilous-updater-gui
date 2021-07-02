package com.hughbone.fabrilousupdater.command.suggestion;

import com.hughbone.fabrilousupdater.util.FabUtil;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class IgnoreListClient implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        FabUtil.createConfigFiles();
        try {
            String line = "";
            BufferedReader file = new BufferedReader(
                    new FileReader(System.getProperty("user.dir") + File.separator + "config" + File.separator + "fabrilous-updater-ignore.txt"));
            while ((line = file.readLine()) != null) {
                builder.suggest(line);
            }
        } catch (IOException e) {}

        return builder.buildFuture();
    }

}