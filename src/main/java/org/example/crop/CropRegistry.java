package org.example.crop;

import org.example.model.CropProfile;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CropRegistry {
    private final Map<String, CropProfile> profiles = new ConcurrentHashMap<>();

    public CropRegistry() {
        registerDefaultLegacyCrops();
    }

    
    private void registerDefaultLegacyCrops() {
        registerCrop(new CropProfile.Builder("Tomato")
                .temperatureRange(22, 28)
                .humidityRange(60, 80)
                .soilMoistureRange(45, Double.MAX_VALUE)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .tempEnergyUsage(10, 15)
                .lowHumidityWaterUsage(20)
                .lowSoilWaterUsage(50)
                .build());

        registerCrop(new CropProfile.Builder("Strawberry")
                .temperatureRange(18, 24)
                .humidityRange(70, 85)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .tempEnergyUsage(10, 15)
                .lowHumidityWaterUsage(25)
                .build());

        registerCrop(new CropProfile.Builder("Lettuce")
                .temperatureRange(15, 21)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .build());

        registerCrop(new CropProfile.Builder("Spinach")
                .temperatureRange(10, 20)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .build());

        registerCrop(new CropProfile.Builder("Chili")
                .temperatureRange(24, 32)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .build());

        registerCrop(new CropProfile.Builder("Cucumber")
                .temperatureRange(18, 27)
                .lightLevelRange(500, Double.MAX_VALUE)
                .nutrientLevelRange(40, Double.MAX_VALUE)
                .build());
    }

    public void registerCrop(CropProfile profile) {
        if (profile != null && profile.getName() != null) {
            profiles.put(profile.getName(), profile);
        }
    }

    public boolean removeCrop(String cropName) {
        if (cropName == null) {
            return false;
        }
        return profiles.remove(cropName) != null;
    }

    public CropProfile getCropProfile(String cropName) {
        return profiles.get(cropName);
    }

    public boolean hasCrop(String cropName) {
        return cropName != null && profiles.containsKey(cropName);
    }

    public Collection<CropProfile> getAllCrops() {
        return Collections.unmodifiableCollection(profiles.values());
    }
}
