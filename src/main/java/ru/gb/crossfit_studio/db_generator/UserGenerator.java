package ru.gb.crossfit_studio.db_generator;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.gb.crossfit_studio.model.Role;
import ru.gb.crossfit_studio.model.User;
import ru.gb.crossfit_studio.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserGenerator {

    private final String[] lastNameMaleBase = new String[]{
            "Иванов", "Петров", "Тарасов", "Владимиров", "Сидоров", "Дмитриев", "Степанов", "Никитин", "Андреев", "Егоров"
            , "Викторов", "Данилов", "Ильин", "Ахмедов", "Федоров"
    };
    private final String[] lastNameFemaleBase = new String[]{
            "Иванова", "Петрова", "Тарасова", "Владимирова", "Сидорова", "Дмитриева", "Степанова", "Никитина", "Андреева", "Егорова"
            , "Викторова", "Данилова", "Ильина", "Ахмедова", "Федорова"
    };
    private final String[] firstNameMaleBase = new String[]{
            "Иван", "Петр", "Тарас", "Владимир", "Сидор", "Дмитрий", "Степан", "Никита", "Андрей", "Егор"
            , "Виктор", "Данил", "Илья", "Ахмед", "Федор"
    };
    private final String[] firstNameFemaleBase = new String[]{
            "Инна", "Полина", "Татьяна", "Виктория", "Светлана", "Дарья", "София", "Наталья", "Алла", "Екатерина"
            , "Арина", "Ксения", "Анна", "Анастасия", "Елена"
    };

    public int getRandomInt(int bound){
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public String generateMaleLastName(){
        return lastNameMaleBase[getRandomInt(lastNameMaleBase.length)];
    }

    public String generateMaleFirstName(){
        return firstNameMaleBase[getRandomInt(firstNameMaleBase.length)];
    }

    public String generateFemaleLastName(){
        return lastNameFemaleBase[getRandomInt(lastNameMaleBase.length)];
    }

    public String generateFemaleFirstName(){
        return firstNameFemaleBase[getRandomInt(firstNameFemaleBase.length)];
    }

    public String generatePhone(){
        String phone = "";
        for (int i = 0; i < 8; i++) {
            phone+=getRandomInt(10);
        }
        return phone;
    }

    public LocalDate generateDateOfBirth() {
        long startEpochDay = LocalDate.of(1960, 1, 1).toEpochDay();
        long endEpochDay = LocalDate.now().minusYears(18).toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }

    public String convertCyrilic(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','е', 'ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц', 'ч', 'ш',   'щ','ы','э', 'ю', 'я','А','Б','В','Г','Д','Е', 'Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч', 'Ш',   'Щ','Ы','Э', 'Ю', 'Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','/','-'};
        String[] abcLat = {" ","a","b","v","g","d","e","yo","zh","z","i","j","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","tsch","y","e","yu","ya","A","B","V","G","D","E","Yo","Zh","Z","I","J","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Tsch","Y","E","Yu","Ya","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","/","-"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

    public String generateEmail(String lastName, String firstName){
        return convertCyrilic(lastName) + "." + convertCyrilic(firstName) + "@mail.com";
    }

    public User generateRandUser(Role role){
        User user = new User();
        int gender = getRandomInt(2);
        if(gender == 0){
            user.setFirstName(generateFemaleFirstName());
            user.setLastName(generateFemaleLastName());
        } else {
            user.setFirstName(generateMaleFirstName());
            user.setLastName(generateMaleLastName());
        }
        user.setDateOfBirth(generateDateOfBirth());
        user.setEmail(generateEmail(user.getLastName(), user.getFirstName()));
        user.setLogin(user.getEmail());
        user.setPassword(passwordEncoder().encode(convertCyrilic(user.getLastName())));
        user.setRole(role);

        return user;
    }

    public List<User> generateUserList(Role role, int num){
        List<User> users = new ArrayList<>();
        while (users.size()<num){
            User user = generateRandUser(role);
            if(!users.contains(user)){
                users.add(user);
            }
        }
        return users;
    }

    public void generateUsersInRepository(UserRepository userRepository, Role role, int num){
//        for (int i = 1; i <= num; i++) {
//            userRepository.save(generateRandUser(role));
//        }
        List<User> users = generateUserList(role, num);
        for (User user : users) {
            userRepository.save(user);
        }
    }
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
