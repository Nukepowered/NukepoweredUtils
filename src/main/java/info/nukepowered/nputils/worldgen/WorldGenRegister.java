package info.nukepowered.nputils.worldgen;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import gregtech.api.GTValues;
import info.nukepowered.nputils.NPULog;
import net.minecraftforge.fml.common.Loader;

public class WorldGenRegister {
	public static void init() {
		long time = System.currentTimeMillis();
		NPULog.info("WorldGen init started");
		
		try {
			copyConfigs();
        } catch (IOException exception) {
            NPULog.fatal("Failed to init worldgen", exception);
        }
		
		NPULog.info(String.format("WorldGen init finished for %.3f seconds", ((System.currentTimeMillis() * 1.0D) / time)));
	}
	
	private static void copyConfigs() throws IOException {
		Path configPath = Loader.instance().getConfigDir().toPath().resolve(GTValues.MODID);
		Path worldgenRootPath = configPath.resolve("worldgen");
		Path jarFileExtractLockOld = configPath.resolve(".npu_worldgen");
        Path jarFileExtractLock = configPath.resolve("npu_worldgen");
        if (!Files.exists(worldgenRootPath)) {
        	Files.createDirectories(worldgenRootPath);
        }
        
        if (!Files.exists(jarFileExtractLock) && !Files.exists(jarFileExtractLockOld) || !Files.list(worldgenRootPath).findFirst().isPresent()) {
        	if (!Files.exists(jarFileExtractLock)) {
        		Files.createFile(jarFileExtractLock);
        	}
        	extractJarVeinDefinitions(worldgenRootPath);
        }
	}
	
	private static void extractJarVeinDefinitions(Path worldgenRootPath) throws IOException {
		FileSystem zipFileSystem = null;
		try {
			URI sampleUri = WorldGenRegister.class.getResource("/assets/gregtech/.gtassetsroot").toURI();
			Path worldgenJarRootPath;
			if (sampleUri.getScheme().equals("jar") || sampleUri.getScheme().equals("zip")) {
				zipFileSystem = FileSystems.newFileSystem(sampleUri, Collections.emptyMap());
				worldgenJarRootPath = zipFileSystem.getPath("/assets/gregtech/worldgen");
			} else if (sampleUri.getScheme().equals("file")) {
				worldgenJarRootPath = Paths.get(WorldGenRegister.class.getResource("/assets/gregtech/worldgen").toURI());
			} else {
				throw new IllegalStateException("Unable to locate absolute path to worldgen root directory: " + sampleUri);
			}
			NPULog.info(String.format("Attempting extraction of standard worldgen definitions from {%s} to {%s}",
					worldgenJarRootPath, worldgenRootPath));
			List<Path> jarFiles = Files.walk(worldgenJarRootPath)
					.filter(jarFile -> Files.isRegularFile(jarFile))
					.collect(Collectors.toList());
			for (Path jarFile : jarFiles) {
				Path worldgenPath = worldgenRootPath.resolve(worldgenJarRootPath.relativize(jarFile).toString());
				Files.createDirectories(worldgenPath.getParent());
				Files.copy(jarFile, worldgenPath, StandardCopyOption.REPLACE_EXISTING);
			}
			NPULog.info(String.format("Extracted {%s} builtin worldgen definitions into worldgen folder", jarFiles.size()));
		} catch (URISyntaxException impossible) {
			throw new RuntimeException(impossible);
		} finally {
			if (zipFileSystem != null) {
				IOUtils.closeQuietly(zipFileSystem);
			}
		}
	}
}
