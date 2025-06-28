package pt.gov.chavemoveldigital.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.District;
import pt.gov.chavemoveldigital.entities.Municipality;
import pt.gov.chavemoveldigital.entities.Parish;
import pt.gov.chavemoveldigital.repositories.DistrictRepository;
import pt.gov.chavemoveldigital.repositories.MunicipalityRepository;
import pt.gov.chavemoveldigital.repositories.ParishRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private MunicipalityRepository municipalityRepository;
    @Autowired
    private ParishRepository parishRepository;


    @Override
    public Long getDistrictsCount() {
        return districtRepository.count();
    }

    @Override
    public Long getMunicipalitiesCount() {
        return municipalityRepository.count();
    }

    @Override
    public Long getParishesCount() {
        return parishRepository.count();
    }


    @PostConstruct
    private void init() throws Exception {

        if (getDistrictsCount() > 0) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("DistrictsMunicipalitiesParishesPortugal.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        Map<String, District> districtsMap = new HashMap<>();
        Map<String, Municipality> municipalitiesMap = new HashMap<>();

        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (line.trim().isEmpty()) continue;

            if (lineNumber == 1) continue;

            String[] parts = line.split(";");
            if (parts.length < 3) continue;

            String districtName = cleanText(parts[0]);
            String municipalityName = cleanText(parts[1]);
            String parishName = cleanText(parts[2]);


            parishName = removeParentheses(parishName);


            if (isEmpty(districtName) || isEmpty(municipalityName) || isEmpty(parishName)) {
                continue;
            }


            District district = districtsMap.get(districtName);
            if (district == null) {
                district = new District();
                district.setName(districtName);
                district.setMunicipalities(new ArrayList<>());
                district = districtRepository.save(district);
                districtsMap.put(districtName, district);
            }


            String municipalityKey = districtName + "_" + municipalityName;
            Municipality municipality = municipalitiesMap.get(municipalityKey);
            if (municipality == null) {
                municipality = new Municipality();
                municipality.setName(municipalityName);
                municipality.setParishes(new ArrayList<>());
                municipality = municipalityRepository.save(municipality);
                municipalitiesMap.put(municipalityKey, municipality);

                district.getMunicipalities().add(municipality);
            }


            String finalParishName = parishName;
            boolean parishExists = parishRepository.findByName(municipalityName)
                    .stream()
                    .anyMatch(parishes -> parishes.getName().equals(finalParishName));

            if (!parishExists) {
                Parish parish = new Parish();
                parish.setName(parishName);
                parishRepository.save(parish);

                municipality.getParishes().add(parish);
            }

        }

        reader.close();

        System.out.println("Distritos: " + getDistrictsCount());
        System.out.println("Municípios: " + getMunicipalitiesCount());
        System.out.println("Freguesias: " + getParishesCount());
    }

    private String cleanText(String text) {
        if (text == null) return null;
        return text.replace("\"", "").trim();
    }

    private String removeParentheses(String text) {
        if (text == null) return null;
        return text.replaceAll("\\s*\\([^)]*\\)", "").trim();
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

}
