package pt.gov.chavemoveldigital.json;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class GenerateUsers {
    private static final int USER_COUNT = 10000;
    private static final String CSV_PATH = "src/main/resources/DistrictsMunicipalitiesParishesPortugal.csv";
    private static final String USERS_JSON_PATH = "src/main/java/pt/gov/chavemoveldigital/json/users.json";

    private static final List<String> FIRST_NAMES = Arrays.asList(
        "João", "Maria", "Ana", "Pedro", "Rita", "Tiago", "Sofia", "Miguel", "Inês", "André", "Carla", "Paulo", "Marta", "Bruno", "Vera", "Luís", "Helena", "Ricardo", "Susana", "Fábio", "Raquel", "Diogo", "Patrícia", "Hugo", "Daniela", "Alexandre", "Beatriz", "Gonçalo", "Leonor", "Francisco", "Eduardo", "Catarina", "Diana", "Mariana", "Cláudia", "Sérgio", "Filipa", "Carlos", "Jorge", "Manuel", "Isabel", "Teresa", "Cristina", "António", "Raul", "Vítor", "Sandra", "Patrícia", "Nuno", "Joana", "Rafael", "Matilde", "Gabriel", "Tomás", "Lara", "Bárbara", "Sílvia", "Aurora", "Salvador", "Eva", "Alice", "Lucas", "Valentina", "Martim", "Dinis", "Simão", "Madalena", "Clara", "Frederico", "Afonso", "Benedita", "Ivo", "Leonardo", "Margarida", "Rúben", "Samuel", "Santiago", "Noah", "David", "Enzo", "Lourenço", "Vicente", "Guilherme", "Henrique", "Tomé", "Caetana", "Bento", "Aurélio", "Ângela", "Célia", "Otávio", "Ângelo", "Emanuel", "Mário", "Jéssica", "Tatiana", "Débora", "Vasco", "Mafalda", "Cristiano", "Bia", "Lídia", "Joaquim", "Rosa", "Celina", "Adriana", "Ema", "Micael", "Micaela", "Rogério", "Sandro", "César", "Duarte", "Benedito", "Júlia", "Aurélia", "Lúcia", "Mónica", "Mirela", "Mirella", "Mireille", "Mireya", "Mirella", "Mireille", "Mireya", "Mirella", "Mireille", "Mireya"
    );
    private static final List<String> LAST_NAMES = Arrays.asList(
        "Silva", "Santos", "Ferreira", "Pereira", "Oliveira", "Costa", "Rodrigues", "Martins", "Jesus", "Sousa", "Fernandes", "Gonçalves", "Gomes", "Lopes", "Marques", "Alves", "Almeida", "Ribeiro", "Pinto", "Carvalho", "Teixeira", "Moreira", "Correia", "Mendes", "Nunes", "Soares", "Vieira", "Monteiro", "Cardoso", "Rocha", "Cruz", "Cunha", "Pires", "Barros", "Machado", "Moura", "Peixoto", "Morais", "Fonseca", "Simões", "Freitas", "Figueiredo", "Neves", "Campos", "Batista", "Borges", "Antunes", "Matos", "Castro", "Azevedo", "Ramos", "Reis", "Coelho", "Tavares", "Domingues", "Aguiar", "Macedo", "Melo", "Pinheiro", "Cordeiro", "Amaral", "Barbosa", "Bastos", "Brito", "Cavalcante", "Cavalcanti", "Chaves", "Coutinho", "Dantas", "Esteves", "Falcão", "Faria", "Garcia", "Leal", "Lima", "Lobo", "Magalhães", "Menezes", "Moraes", "Nascimento", "Paiva", "Queirós", "Rangel", "Rezende", "Sá", "Salgado", "Sampaio", "Saraiva", "Seabra", "Serra", "Soares", "Teles", "Torres", "Valente", "Varela", "Vasconcelos", "Viana", "Xavier"
    );

    static class User {
        String telephoneNumber;
        int pin;
        int nif;
        String firstName;
        String lastName;
        String district;
        String municipality;
        String parish;
    }

    static class Location {
        String district;
        String municipality;
        String parish;
        Location(String d, String m, String p) {
            this.district = d;
            this.municipality = m;
            this.parish = p;
        }
    }

    public static void main(String[] args) throws Exception {
        List<Location> locations = loadLocations();
        Collections.shuffle(locations);
        Set<String> usedFirstNames = new HashSet<>();
        Set<String> usedLastNames = new HashSet<>();
        Set<String> usedTelephones = new HashSet<>();
        Set<Integer> usedPins = new HashSet<>();
        Set<Integer> usedNifs = new HashSet<>();
        List<String> availableFirstNames = new ArrayList<>(FIRST_NAMES);
        List<String> availableLastNames = new ArrayList<>(LAST_NAMES);
        Collections.shuffle(availableFirstNames);
        Collections.shuffle(availableLastNames);
        Random rand = new Random();

        // Carregar utilizadores existentes
        List<User> users = loadExistingUsers();
        int startNif = 200000000 + rand.nextInt(10000000);
        int startPin = 1000 + rand.nextInt(8000);
        int startPhone = 910000000 + rand.nextInt(80000000);

        for (int i = 0; i < USER_COUNT; i++) {
            // Garantir nomes únicos
            String firstName = pickUnique(availableFirstNames, usedFirstNames, rand);
            String lastName = pickUnique(availableLastNames, usedLastNames, rand);
            // Garantir NIF, PIN, telefone únicos
            int nif = nextUniqueInt(usedNifs, startNif + i);
            int pin = nextUniqueInt(usedPins, startPin + i);
            String telephone = nextUniquePhone(usedTelephones, startPhone + i);
            // Localização
            Location loc = locations.get(i % locations.size());
            User user = new User();
            user.telephoneNumber = telephone;
            user.pin = pin;
            user.nif = nif;
            user.firstName = firstName;
            user.lastName = lastName;
            user.district = loc.district;
            user.municipality = loc.municipality;
            user.parish = loc.parish;
            users.add(user);
        }
        // Guardar no ficheiro
        saveUsers(users);
        System.out.println("Utilizadores gerados e adicionados ao users.json!");
    }

    private static List<Location> loadLocations() throws IOException {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CSV_PATH), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // header
            Pattern paren = Pattern.compile("\\s*\\(.*?\\)");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 3) continue;
                String parish = paren.matcher(parts[2]).replaceAll("").trim();
                locations.add(new Location(parts[0], parts[1], parish));
            }
        }
        return locations;
    }

    private static List<User> loadExistingUsers() throws IOException {
        File file = new File(USERS_JSON_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
        }
    }

    private static void saveUsers(List<User> users) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(USERS_JSON_PATH), StandardCharsets.UTF_8)) {
            gson.toJson(users, writer);
        }
    }

    private static String pickUnique(List<String> available, Set<String> used, Random rand) {
        for (int i = 0; i < available.size(); i++) {
            String val = available.get(i);
            if (!used.contains(val)) {
                used.add(val);
                return val;
            }
        }
        // Se esgotar, gera um nome aleatório
        String val = "Nome" + rand.nextInt(1000000);
        used.add(val);
        return val;
    }

    private static int nextUniqueInt(Set<Integer> used, int candidate) {
        while (used.contains(candidate)) candidate++;
        used.add(candidate);
        return candidate;
    }

    private static String nextUniquePhone(Set<String> used, int candidate) {
        String phone = "+351" + candidate;
        while (used.contains(phone)) {
            candidate++;
            phone = "+351" + candidate;
        }
        used.add(phone);
        return phone;
    }
}

