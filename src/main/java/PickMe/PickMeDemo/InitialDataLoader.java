package PickMe.PickMeDemo;

import PickMe.PickMeDemo.entity.*;
import PickMe.PickMeDemo.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PortfolioRepository portfolioRepository;
    private final PostsRepository postsRepository;
    private final CategoryRepository categoryRepository;
    private final VectorSimilarityRepository vectorSimilarityRepository;
    private final PostsFilesRepository postsFilesRepository;


    private void createUserAndPortfolio(
            String userName,
            String nickName,
            String email,
            String password,
            int web,
            int app,
            int game,
            int ai,
            String shortIntroduce,
            String introduce,
            String fileUrl
    ) {
        User user = User.builder()
                .userName(userName)
                .nickName(nickName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.now()).build();

        userRepository.save(user);

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .web(web)
                .app(app)
                .game(game)
                .ai(ai)
                .shortIntroduce(shortIntroduce)
                .introduce(introduce)
                //.fileUrl(fileUrl)
                .build();

        portfolioRepository.save(portfolio);
    }
    private User createUserAndPortfolio_Profile(
            String userName,
            String nickName,
            String email,
            String password,
            int web,
            int app,
            int game,
            int ai,
            String shortIntroduce,
            String introduce,
            String fileUrl,
            String imageUrl
    ) {
        User user = User.builder()
                .userName(userName)
                .nickName(nickName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.now())
                .imageUrl(imageUrl).build();

        userRepository.save(user);

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .web(web)
                .app(app)
                .game(game)
                .ai(ai)
                .shortIntroduce(shortIntroduce)
                .introduce(introduce)
                //.fileUrl(fileUrl)
                .build();

        portfolioRepository.save(portfolio);

        return user;
    }

    public void createStudyPosts(final User user, final String title, int recruitmentCount, final String content, LocalDate endDate, boolean web, boolean app, boolean game, boolean ai ){

        Posts posts = Posts.builder()
                .user(user)
                .postType(PostType.STUDY)
                .title(title)
                .recruitmentCount(recruitmentCount)
                //.counts(1)
                .content(content)
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate)
                .build();

        postsRepository.save(posts);

        Category category = Category.builder()
                .posts(posts)
                .web(web)
                .app(app)
                .game(game)
                .ai(ai)
                .build();

        category.validateFieldCount();
        categoryRepository.save(category);

    }
    public void createProjectPosts(final User user, final String title, int recruitmentCount, final String content, LocalDate endDate, boolean web, boolean app, boolean game, boolean ai ){

        Posts posts = Posts.builder()
                .user(user)
                .postType(PostType.PROJECT)
                .title(title)
                .recruitmentCount(recruitmentCount)
                //.counts(1)
                .content(content)
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate)
                .build();

        postsRepository.save(posts);

        Category category = Category.builder()
                .posts(posts)
                .web(web)
                .app(app)
                .game(game)
                .ai(ai)
                .build();

        category.validateFieldCount();
        categoryRepository.save(category);

    }

    // 여기서 createRecommendationsTable 함수는 Recommendations 테이블에 데이터를 저장하도록 되어 있다.

    public static List<List<Integer>> getPermutations(List<Integer> numbers) {
        Collections.sort(numbers);
        List<List<Integer>> permutations = new ArrayList<>();
        do {
            permutations.add(new ArrayList<>(numbers));
        } while (nextPermutation(numbers));
        return permutations;
    }

    public static boolean nextPermutation(List<Integer> arr) {
        int n = arr.size();
        int i = n - 2;
        while (i >= 0 && arr.get(i) >= arr.get(i + 1)) {
            i--;
        }
        if (i < 0) {
            return false;
        }
        int j = n - 1;
        while (arr.get(j) <= arr.get(i)) {
            j--;
        }
        Collections.swap(arr, i, j);
        Collections.reverse(arr.subList(i + 1, n));
        return true;
    }

    public static Integer[] listToVector(List<Integer> list) {
        Integer[] vector = new Integer[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vector[i] = list.get(i);
        }
        return vector;
    }

    private double calculateCosineSimilarity(Integer[] vectorA, Integer[] vectorB) {
        // Calculate the dot product of vectorA and vectorB
        double dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            magnitudeA += Math.pow(vectorA[i], 2);
            magnitudeB += Math.pow(vectorB[i], 2);
        }

        // Calculate the magnitude (Euclidean norm) of each vector
        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        // Calculate the cosine similarity
        if (magnitudeA == 0 || magnitudeB == 0) {
            // Handle the case of zero magnitude (avoid division by zero)
            return 0.0;
        } else {
            return dotProduct / (magnitudeA * magnitudeB);
        }
    }



    @Override
    public void run(String... args) throws Exception {

//        // 초기 벡터값 이니셜라이징
//        int dimension = 4;
//        int maxValue = 4; // Values can range from 0 to 4
//        generateAndSaveSimilarityData(dimension, maxValue);

        String initialImg = "comgongWow.png";

        List<List<Integer>> standardPermutations = new ArrayList<>();
        standardPermutations.add(List.of(0, 0, 0, 0));
        standardPermutations.add(List.of(1, 0, 0, 0));
        standardPermutations.add(List.of(2, 0, 0, 0));
        standardPermutations.add(List.of(3, 0, 0, 0));
        standardPermutations.add(List.of(4, 0, 0, 0));
        standardPermutations.add(List.of(2, 1, 0, 0));
        standardPermutations.add(List.of(3, 1, 0, 0));
        standardPermutations.add(List.of(3, 2, 0, 0));
        standardPermutations.add(List.of(4, 1, 0, 0));
        standardPermutations.add(List.of(4, 2, 0, 0));
        standardPermutations.add(List.of(4, 3, 0, 0));
        standardPermutations.add(List.of(3, 2, 1, 0));
        standardPermutations.add(List.of(4, 2, 1, 0));
        standardPermutations.add(List.of(4, 3, 1, 0));
        standardPermutations.add(List.of(4, 3, 2, 0));
        standardPermutations.add(List.of(4, 3, 2, 1));

        List<List<Integer>> standardPermutationsList = new ArrayList<>();
        for (List<Integer> standardPermutation : standardPermutations) {
            standardPermutationsList.addAll(getPermutations(new ArrayList<>(standardPermutation)));
        }

        List<List<Integer>> numbers = new ArrayList<>();
        numbers.add(List.of(0, 0, 0, 0));
        numbers.add(List.of(1, 0, 0, 0));
        numbers.add(List.of(2, 0, 0, 0));
        numbers.add(List.of(3, 0, 0, 0));
        numbers.add(List.of(4, 0, 0, 0));
        numbers.add(List.of(2, 1, 0, 0));
        numbers.add(List.of(3, 1, 0, 0));
        numbers.add(List.of(3, 2, 0, 0));
        numbers.add(List.of(4, 1, 0, 0));
        numbers.add(List.of(4, 2, 0, 0));
        numbers.add(List.of(4, 3, 0, 0));
        numbers.add(List.of(3, 2, 1, 0));
        numbers.add(List.of(4, 2, 1, 0));
        numbers.add(List.of(4, 3, 1, 0));
        numbers.add(List.of(4, 3, 2, 0));
        numbers.add(List.of(4, 3, 2, 1));

        for (List<Integer> standard : standardPermutationsList) {

            List<List<Integer>> permutations = new ArrayList<>();

            for (List<Integer> number : numbers) {
                permutations.addAll(getPermutations(new ArrayList<>(number)));
            }

            for (List<Integer> permutation : permutations) {
                Integer[] standardArray = listToVector(standard);
                Integer[] permutationArray = listToVector(permutation);

                double similarity = calculateCosineSimilarity(permutationArray, standardArray);

                VectorSimilarity vectorSimilarity = new VectorSimilarity(standardArray, permutationArray, similarity);

                vectorSimilarityRepository.save(vectorSimilarity);
            }
        }


        // 초기 데이터 생성 및 저장(관리자)
        User adminUser = User.builder()
                .userName("운영자")
                .nickName("admin")
                .email("admin@g.hongik.ac.kr")
                .password(passwordEncoder.encode("admin"))  // 비밀번호 해싱
                .role(Role.ADMIN)
                .lastAccessDate(LocalDateTime.of(2023, 9, 27, 14, 30, 0))
                .build();

        userRepository.save(adminUser);


        // 초기 데이터 생성 및 저장(유저)
        User generalUser = User.builder()
                .userName("김유저")
                .nickName("user")
                .email("user@g.hongik.ac.kr")
                .password(passwordEncoder.encode("user"))  // 비밀번호 해싱
                .imageUrl("profile3.png")
                .role(Role.USER)
                .build();

        userRepository.save(generalUser);

        // 포트폴리오 생성자
        // public Portfolio(User user, Integer web, Integer app, Integer game, Integer ai, String shortIntroduce, String introduce, String fileUrl)
        Portfolio generalPortfolio = Portfolio.builder()
                .user(generalUser)
                .web(4)
                .app(3)
                .game(2)
                .ai(1)
                .shortIntroduce("안녕하세요, 웹과 앱에 관심있는 코딩 꿈나무입니다.")
                .introduce("- 맛있홍 프로젝트 (React + Node.js + Express.js)" +
                        "\n- 픽미 프로젝트 (React + SpringBoot + JPA)")
                //.fileUrl("")
                .build();


        portfolioRepository.save(generalPortfolio);


// =======================================================================================================
// =======================================================================================================

        String initialEndDate100 = "2024-01-02";
        DateTimeFormatter dateFormatter100 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate100 = LocalDate.parse(initialEndDate100, dateFormatter100);

        String name ="";
        String nickName = "";
        String email = "";

        String shortInt = "언제든지 열정페이";
        String introduce = "- 한영외고 \n" +
                "- 홍익대학교 영어교육과 \n" +
                "- 영어 토론대회 우수상 \n" +
                "\n" +
                "컴퓨터공학과 복수전공중입니다! \n" +
                "자료조사랑 보고서 쓰는거 잘합니다. 함께 열심히 해보아요:)";

        String postTitle = "졸업 프로젝트 팀원 구합니다";
        String content = "저는 졸업이 목표고 현재 인턴중이라 졸업 프로젝트에 크게 시간을 쏟기 어렵습니다. 저와 상황이 비슷하신 분과 함께 팀을 이루고싶습니다." +
                "\n 관심 있으신 분은 댓글 혹은 연락 부탁드립니다";



        User user = createUserAndPortfolio_Profile(
                "김길동", "killdong", "killdong@g.hongik.ac.kr",
                "1", 3, 2, 0, 0, shortInt, introduce, null, "profile70.jpg"

        );
        createProjectPosts(user,postTitle, 1, content , endDate100, true, false, false, true);


        // =======
        // =======

        initialEndDate100 = "2024-01-03";
        endDate100 = LocalDate.parse(initialEndDate100, dateFormatter100);


        // =========
        // =========
        name = "강길동";
        nickName = "gillfin";
        email = "gillfin@g.hongik.ac.kr";


        shortInt = "믿고 맡기는 백준플레";
        introduce = "코딩노예의 삶을 살아왔으나 아직 플젝 경험은 없음 \n" +
                "주 언어는 C++이지만 python도 어느정도는 쓸 수 있습니다\n" +
                "코테 준비를 오래 해서 알고리즘 문제는 웬만해서 다 해결 가능합니다\n" +
                "\n" +
                "현재 3학년이고 내년에 플젝으로 게임, 웹, 인공지능 중에서 하나로 생각중입니다. \n" +
                "관심있으신 분은 제 개인 연락처로 연락 부탁드립니다\n" +
                "";

        postTitle = "포폴 같이 만들어보실 분?";
        content = "코드 공부만 하고 실제로 만들어본 경험 없으신 분들 같이 플젝 구상부터 완성까지 해봐용\n\n"
                +"저도 경험이 막 많은 건 아니라 많은걸 바라지는 못하고....같이 열심히 새보실 분 찾습니다\n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 4, 0, 3, 2, shortInt, introduce, null, "profile71.jpg"

                )
                ,postTitle, 3, content , endDate100, true, false, true, false);


        initialEndDate100 = "2024-01-20";
        endDate100 = LocalDate.parse(initialEndDate100, dateFormatter100);


        // ========
        // ========
        name = "구길동";
        nickName = "fishgill";
        email = "fishgill@g.hongik.ac.kr";


        shortInt = "절대 던지지 않습니다";
        introduce = "희망사항 : 졸업 전에 쇼핑몰 사이트 런칭하기!! \n" +
                "\n" +
                "프론트로 vue.js 와 react 사용 가능합니다!\n" +
                "현재 쇼핑몰 웹사이트 제작중입니당";

        postTitle = "쇼핑몰 사이트 플젝 팀원 구합니다. 현재 디자이너와 프론트 있음";
        content = "빈티지 쇼핑몰 사이트 프로젝트 백엔드 개발자 모십니다\n"
                +"-현재 저(프론트)와 디자이너(UIUX) 두 명이서 프로젝트 구상은 마친 상태입니다.\n"
                +"-프론트는 React로 짜여졌으며 결과에 따라서 창업까지도 준비중입니다. \n"
                +"-함께 오랫동안 프로젝트 이어가실 컴퓨터공학과 학우분 모십니다. \n\n"
                +"문의사항 있으시면 댓글 혹은 제 포트폴리오 개인 연락처로 문의 브탁드립니다 \n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 4, 0, 3, 2, shortInt, introduce, null, "profile72.jpg"

                )
                ,postTitle, 4, content , endDate100, true, false, true, false);


        // ========
        // ========
        name = "이길동";
        nickName = "ppry";
        email = "ppry@g.hongik.ac.kr";


        shortInt = "게임 만들고싶다…...";
        introduce = "- 3D 게임, 언리얼, unity 관심 있습니다.\n" +
                "- Unity보다는 언리얼 선호하며 공부중입니다.\n" +
                "\n" +
                "학점은 4.2/4.5 컴퓨터공학과 20학번…...";

        postTitle = "게임 프로직트 팀원 모집합니다‍🔥";
        content = "클론코딩부터 시작해서 같이 공부하고 잘되면 실제로 앱으로 출시까지 할 게임 개발 팀원 구합니다\n\n"
                +"주제는 아직 생각해둔 것은 없지만, 팀원이 생기면 함께 회의해서 맞춰나가고싶습니다\n"
                +"3학년이라 졸업프로젝트도 생각해야돼서, 잘되면 졸업 프로젝트까지도 함께 해결하면 좋을 것 같습니다.\n"
                +"많은 관심 부탁드립니다\n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 4, 2, shortInt, introduce, null, "profile73.jpg"

                )
                ,postTitle, 4, content , endDate100, false, false, true, false);


        // ========
        // ========
        name = "오길동";
        nickName = "leoh";
        email = "leoh@g.hongik.ac.kr";


        shortInt = "FE/BE 다 함";
        introduce = "=프론트는 Vue 주로 써봤으나 React도 가능합니다\uD83D\uDE01\n" +
                "-백엔드는 Spring Boot 개발 경험 있습니다. 그런데 주력 언어는 python인 그런 상태 ㅋㅋㅋ \n" +
                "\n\n" +
                "이것저것 찍먹 개발자(희망)입니다!! 예쁘게 봐주십셔~~~\n" +
                "참고로 4학년, 취준, 플젝 급함!!!";

        postTitle = "웹 스터디";
        content = "이고잉, 김영한 커리 같이 타실 분들 모집합니다.\n"
                + "-인프런 강의 같이 결제하거나 따로 결제하거나 저는 다 좋습니다.\n"
                +"-매주 시간 정해서 학교 카페에서 1시간씩 서로 질문하고 진도 체크하는 시간 가지려고 합니다\n"
                +"-자세한 일정은 스터디원이 충분히 모이면 다시 정하면 좋을 것 같습니다.\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 4, 2, shortInt, introduce, null, "profile74.jpg"

                )
                ,postTitle, 4, content , endDate100, false, false, true, false);




        // ========
        // ========
        name = "우길동";
        nickName = "우동사리";
        email = "udon@g.hongik.ac.kr";


        shortInt = "언리얼 공부중";
        introduce = "시각디자인과랑 협업프로젝트로 3D 게임 개발 경력 있어요!\n" +
                "FPS 게임 개발 했었구 앱스토어에서 \"\"@@@\"\"로 검색하면 다운받을 수도 있습니다!\n" +
                "\n" +
                "저는 주로 클라이언트 담당했었어요! \n" +
                "자세한건 제 깃허브 구경와주세요 \uD83D\uDE06";

        postTitle = "마음 맞는 게임 개발 팀원 찾아요~!";
        content = "개인적으로 제가 게임에 진심이고 미쳐서 저와 성향과 관심사가 비슷한 분들끼리 게임 개발하고싶습니다\n"
                +"제 경력은 제 개인 포트폴리오에서 확인하실 수 있습니당!\n\n"
                +"함께 진심으로 즐기는 마음으로 게임 제작할 수 있는 팀원을 찾고있습니다!\n"
                +"플젝에 관해 궁금하신 점 있으시면 편하게 문의 주세요!\n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 4, 0, shortInt, introduce, null, "profile75.jpg"

                )
                ,postTitle, 4, content , endDate100, false, false, true, false);


        // ========
        // ========
        name = "어길동";
        nickName = "findingme";
        email = "findingme@g.hongik.ac.kr";


        shortInt = "공모전 도장깨기!";
        introduce = "한번이라도 공모전에서 상 받고싶다~~~";

        postTitle = "2학기 교내 공모전 같이 나가실 분?";
        content = "교내에 2학기마다 공모전? 경진대회? 있는걸로 알고있습니다\n"
                +"그거 같이 나가실 분 찾습니다. 학년, 과, 복수전공 이런거 신경 안 씁니다. 같이 할 실력이 있는 분이면 제가 다 맞출 수 있습니다\n"
                +"공모전 수상이 목표이신 분들은 연락 주세요\n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 3, 4, shortInt, introduce, null, "profile76.jpg"

                )
                ,postTitle, 3, content , endDate100, false, false, true, true);

        // ========
        // ========
        name = "유길동";
        nickName = "youyisi";
        email = "youyisi@g.hongik.ac.kr";


        shortInt = "알골장인";
        introduce = "코딩 적성은 잘 몰겠지만 알골문제 푸는게 재밌는건 아는 컴퓨터공학과";

        postTitle = "네이버 코테 스터디";
        content = "함께 네이버 코테 준비할 사람 있음?";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 4, 0, 0, 0, shortInt, introduce, null, "profile77.jpg"

                )
                ,postTitle, 3, content , endDate100, true, false, false, false);


        // ========
        // ========
        name = "양길동";
        nickName = "lamblamb";
        email = "lamblamb@g.hongik.ac.kr";


        shortInt = "말하는 감쟈에오";
        introduce = "학교 수업은 열심히 들었지만 아직 프로젝트 경험은 없습니다. \n" +
                "수업 열심히 들어서 배경지식은 탄탄하기 때문에 어떤 프로젝트든 시작하면 잘 할 수 있습니다!\n" +
                "\n" +
                "참고로 전공평점 4.0입니다";

        postTitle = "졸프";
        content = "졸프 팀원 찾아요..... \n" +
                "일단 저는 졸업이 목표입니다\n" +
                "\n" +
                "같이 하실 분 찾습니다.....댓글 주세용";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 3, 4, 0, 0, shortInt, introduce, null, "profile78.jpg"

                )
                ,postTitle, 3, content , endDate100, true, true, false, false);


        // ========
        // ========
        name = "마길동";
        nickName = "strong_gd";
        email = "strong_gd@g.hongik.ac.kr";


        shortInt = "붓싼 싸나이, 코딩, 쉽다";
        introduce = "길게 말 안한다. Python, 인공지능 좋아함 \n" +
                "깃허브 잔디밭임";

        postTitle = "인공지능 스터디";
        content = "파이토치 공부중\n" +
                "딥러닝 같이 공부할 학우 찾음\n" +
                "형은 하나만 판다.\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 0, 4, shortInt, introduce, null, "profile79.jpg"

                )
                ,postTitle, 4, content , endDate100, false, false, false, true);

        // ========
        // ========
        name = "박길동";
        nickName = "changingill";
        email = "changingill@g.hongik.ac.kr";


        shortInt = "A길만 걸어옴";
        introduce = "- 2019년도 홍익대학교 알고리즘경진대회 수상 \n" +
                "- 2019년도 홍익대학교 창의적경진대회 수상 \n" +
                "- 2020년도 1학기 컴퓨터공학과 과수석 \n" +
                "- 2020년도 CJ 공모전 수상";

        postTitle = "취준 스터디";
        content = "서합 현재까지 3군데\n" +
                "같이 면접 준비할 학우 구합니다.\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 4, 0, 0, 3, shortInt, introduce, null, "profile80.jpg"

                )
                ,postTitle, 4, content , endDate100, true, false, false, true);



        // ========
        // ========
        name = "배길동";
        nickName = "pearof_ways";
        email = "pearof_way@g.hongik.ac.krs";


        shortInt = ".";
        introduce = ".";

        postTitle = "cs스터디";
        content = "다음학기에 알골, 프언, 컴네 듣는데 미리 예습하려고 합니다.\n" +
                "같이 방학동안 스터디 신청해서 지원금 받으면서 공부할 학우분 찾습니다.\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 0, 0, 3, shortInt, introduce, null, "profile81.jpg"

                )
                ,postTitle, 4, content , endDate100, false, false, false, true);


        // ========
        name = "방길동";
        nickName = "fartingfin";
        email = "fartingfin@g.hongik.ac.kr";


        shortInt = "졸업이 목표";
        introduce = "쓸 수 있는게 없다ㅠㅠㅠㅠㅠㅠ";

        postTitle = "공기업 NCS 스터디";
        content = "전공이 너어어무 안 맞아서 다른 분야로 공기업 준비중입니다\n" +
                "함께 같이 공부할 팀원 있을까요? ㅠㅠ\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 0, 1, 0, 0, shortInt, introduce, null, "profile82.jpg"

                )
                ,postTitle, 4, content , endDate100, false, true, false, false);



        // ========
        name = "봉길동";
        nickName = "hogubbong";
        email = "hogubbong@g.hongik.ac.kr";


        shortInt = "아싸 구제좀…..";
        introduce = "복전생입니다!!\n" +
                "수학 오랜만에 보니 새로와요ㅠ \n" +
                "불쌍한 중생 구제 부탁드려요";

        postTitle = "msc 스터디!";
        content = "문과 자전 공대생인데 msc 때문에 고생중이에요ㅠㅠㅠㅠ\n" +
                "저처럼 문과였거나 미적 기벡 선택 안하신 분들 중에서 같이 msc 공부하실 분 있으면 같이 공부하면 좋을 것 같아요!\n"
                +"같이 으쌰으쌰 힘내서 다음 학기에 올A+ 받아봐여!!!\n";


        createStudyPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 2, 1, 0, 0, shortInt, introduce, null, "profile83.jpg"

                )
                ,postTitle, 4, content , endDate100, true, true, false, false);




        // ========
        name = "시길동";
        nickName = "civilsigil";
        email = "civilsigil@g.hongik.ac.kr";


        shortInt = "ㅎㅎ…닳고 닳은 팀플장인";
        introduce = "산업디자인과, 컴퓨터공학과 복수전공 \n" +
                "- 산업디자인과 소모임 팀플 경력만 3년째 \n" +
                "- 그 외 교양수업 다수 발표기계, 피피티장인 \n" +
                "- 자료조사, 컨셉지정, 스케쥴 관리 다 함 \n" +
                "- 학교 다니면서 별별 사람 다 봤음.\n"+
                "- 본인 성격이 개같다? 신경 안씀ㅇㅇ 더한 사람도 겪어봤음\n" +
                "- 만능 조장, 다만 코드는 아직 좀 부족하지만 금방 할 수 있을 것 같습니다";

        postTitle = "팀 경험 쌓으실 분?";
        content = "컨셉, 디자인은 걱정 안 하셔도 됨. 제가 디자인과라 그런 쪽은 몸만 오셔도 됨\n" +
                "+ 웹쪽 플젝 경험 쌓고싶지만 게임도 가능함니당~\n"
                +"+ 컨셉부터 쭉 함께 플젝 만드는 경험 쌓으실 분들 댓글 주세용\n";


        createProjectPosts(
                createUserAndPortfolio_Profile(
                        name, nickName, email,
                        "1", 4, 1, 3, 0, shortInt, introduce, null, "profile85.jpg"

                )
                ,postTitle, 4, content , endDate100, true, false, true, false);


// =======================================================================================================
// =======================================================================================================


        // 초기 데이터 생성 및 저장(1)
        // 모든 값이 비어있어서 생성 실험 가능한 유저
        User user1 = User.builder()
                .userName("김실험")
                .nickName("TestKim")
                .email("testKim@g.hongik.ac.kr")
                .password(passwordEncoder.encode("1"))  // 비밀번호 해싱
                .imageUrl("profile20.png")
                .role(Role.USER)
                .build();

        userRepository.save(user1);


        // 초기 데이터 생성 및 저장(2)
        User user2 = User.builder()
                .userName("이문식")
                .nickName("croco1997")
                .email("croco1997@g.hongik.ac.kr")
                .password(passwordEncoder.encode("2"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile1.png")
                .build();

        userRepository.save(user2);

        Portfolio user2Portfolio = Portfolio.builder()
                .user(user2)
                .web(4)
                .app(3)
                .game(2)
                .ai(1)
                .shortIntroduce("안녕하세요, 웹과 앱에 관심있는 코딩 꿈나무입니다.")
                .introduce("- 맛있홍 프로젝트 (React + Node.js + Express.js) \n- 픽미 프로젝트 (React + SpringBoot + JPA) \n- 코로나 보드 크롤링 프로젝트(Node.js + Express.js)")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user2Portfolio);

        String initialEndDate1 = "2023-11-30"; // 원하는 종료 날짜를 스트링으로 받음
        DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");   // 날짜 포맷터를 사용하여 날짜 문자열을 'LocalDate' 개체로 변환
        LocalDate endDate1 = LocalDate.parse(initialEndDate1, dateFormatter1);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts1 = Posts.builder()
                .user(user2)
                .postType(PostType.PROJECT)
                .title("졸프 팀원 구해요~")
                .recruitmentCount(3)
                //.counts(1)
                .content("졸업 프로젝트 팀원을 모집합니다.\n현재 저는 풀스택 개발 가능하고, Spring Boot 가능한 백엔드 개발자 한 분과, React 및 Redux 사용 가능한 프론트 개발자 두 분을 모십니다.\n언제든지 연락 주세요!")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate1)
                .build();

        postsRepository.save(posts1);

        Category category1 = Category.builder()
                .posts(posts1)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category1.validateFieldCount();
        categoryRepository.save(category1);

        String initialEndDate2 = "2023-12-10";
        DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate2 = LocalDate.parse(initialEndDate2, dateFormatter2);

        Posts posts2 = Posts.builder()
                .user(user2)
                .postType(PostType.PROJECT)
                .title("토이 플젝 하실분?")
                .recruitmentCount(2)
                //.counts(1)
                .content("토이 프로젝트 팀원을 모집합니다.\n주제는 아직 정해지지 않았습니다.\n현재 저는 백엔드 개발 가능하고, React 및 Redux 사용 가능한 프론트 개발자 두 분을 모십니다.")
                //.promoteImageUrl("사진 없음")
                //.fileUrl("파일 없음")
                .endDate(endDate2)
                .build();

        postsRepository.save(posts2);

        Category category2 = Category.builder()
                .posts(posts2)
                .web(true)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category2.validateFieldCount();
        categoryRepository.save(category2);

        String initialEndDate3 = "2023-11-16";
        DateTimeFormatter dateFormatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate3 = LocalDate.parse(initialEndDate3, dateFormatter3);

        Posts posts3 = Posts.builder()
                .user(user2)
                .postType(PostType.STUDY)
                .title("배자구 스터디")
                .recruitmentCount(4)
                //.counts(1)
                .content("배자구 스터디 구합니다.\n감자도 환영합니다.\n저도 자구알못이에요..ㅠㅠ\n같이 자구 공부해요!!")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate3)
                .build();

        postsRepository.save(posts3);

        Category category3 = Category.builder()
                .posts(posts3)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category3.validateFieldCount();
        categoryRepository.save(category3);

        // 초기 데이터 생성 및 저장(3)
        User user3 = User.builder()
                .userName("박시홍")
                .nickName("freshHongsi")
                .email("hongsi@g.hongik.ac.kr")
                .password(passwordEncoder.encode("3"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 27, 14, 30, 0))
                .imageUrl("profile19.png")
                .build();

        userRepository.save(user3);

        Portfolio user3Portfolio = Portfolio.builder()
                .user(user3)
                .web(4)
                .app(3)
                .game(0)
                .ai(0)
                .shortIntroduce("풀스택 개발자 그 자체, 홍시입니다.")
                .introduce("- 맛있홍 프로젝트 (React + Node.js + Express.js) \n- 픽미 프로젝트 (React + SpringBoot + JPA) \n- 코로나 보드 크롤링 프로젝트(Node.js + Express.js)")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user3Portfolio);

        String initialEndDate4 = "2023-12-12"; // 원하는 종료 날짜를 스트링으로 받음
        DateTimeFormatter dateFormatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd");   // 날짜 포맷터를 사용하여 날짜 문자열을 'LocalDate' 개체로 변환
        LocalDate endDate4 = LocalDate.parse(initialEndDate4, dateFormatter4);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts4 = Posts.builder()
                .user(user3)
                .postType(PostType.PROJECT)
                .title("권교수님과 졸프 하실분!")
                .recruitmentCount(3)
                //.counts(1)
                .content("권건우 교수님과 함께할 졸업 프로젝트 팀원을 모집합니다.\n주제는 먹거리 관련 입니다.\n현재 저는 풀스택 개발 가능하고, Spring Boot 가능한 백엔드 개발자 한 분과, React 및 Redux 사용 가능한 프론트 개발자 한 분을 모십니다.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate4)
                .build();

        postsRepository.save(posts4);

        Category category4 = Category.builder()
                .posts(posts4)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category4.validateFieldCount();
        categoryRepository.save(category4);

        String initialEndDate5 = "2023-11-23";
        DateTimeFormatter dateFormatter5 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate5 = LocalDate.parse(initialEndDate5, dateFormatter5);

        Posts posts5 = Posts.builder()
                .user(user3)
                .postType(PostType.STUDY)
                .title("알골 스터디 팀원 구함")
                .recruitmentCount(4)
                //.counts(1)
                .content("알고리즘 스터디 구함.\n란골 배골 곤골 모두 환영.\n백준 플래티넘이 목표.\n알고리즘 마스터가 되어봅시다..")
                //.promoteImageUrl("사진 없음")
                //.fileUrl("파일 없음")
                .endDate(endDate5)
                .build();

        postsRepository.save(posts5);

        Category category5 = Category.builder()
                .posts(posts5)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category5.validateFieldCount();
        categoryRepository.save(category5);


        String initialEndDate6 = "2023-12-20";
        DateTimeFormatter dateFormatter6 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate6 = LocalDate.parse(initialEndDate6, dateFormatter6);

        Posts posts6 = Posts.builder()
                .user(user3)
                .postType(PostType.STUDY)
                .title("프린스 송")
                .recruitmentCount(3)
                //.counts(1)
                .content("송프언 프롤로그, 렉스, 야크, 리스프 과제 같이 고민해요!\n열심히 하시는 분들 환영!")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate6)
                .build();

        postsRepository.save(posts6);

        Category category6 = Category.builder()
                .posts(posts6)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category6.validateFieldCount();
        categoryRepository.save(category6);

        // 초기 데이터 생성 및 저장(4)
        User user4 = User.builder()
                .userName("Black Consumer")
                .nickName("악성유저")
                .email("blackComsumer@g.hongik.ac.kr")
                .password(passwordEncoder.encode("4"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 6, 27, 14, 30, 0))
                .imageUrl("profile2.png")
                .build();

        userRepository.save(user4);

        Portfolio user4Portfolio = Portfolio.builder()
                .user(user4)
                .web(0)
                .app(0)
                .game(0)
                .ai(0)
                .shortIntroduce("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.짧은소개글이지만길게쓴다.왜냐면난악성유저니까...")
                .introduce("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.경력도길게쓴다.왜냐면난악성유저니까...")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user4Portfolio);

        // 악성유저는 모집 기간이 이미 지난 날짜로 세팅되어있음.
        String initialEndDate7 = "2023-08-15"; // 원하는 종료 날짜를 스트링으로 받음
        DateTimeFormatter dateFormatter7 = DateTimeFormatter.ofPattern("yyyy-MM-dd");   // 날짜 포맷터를 사용하여 날짜 문자열을 'LocalDate' 개체로 변환
        LocalDate endDate7 = LocalDate.parse(initialEndDate7, dateFormatter7);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts7 = Posts.builder()
                .user(user4)
                .postType(PostType.PROJECT)
                .title("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.제목도길게쓴다.왜냐면난악성유저니까..." +
                        "제목도내맘대로두번이나쓴다ㅋㅋ.나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.제목도길게쓴다.왜냐면난악성유저니까...")
                .recruitmentCount(2)
                //.counts(1)
                .content("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까...")
                //.promoteImageUrl("나는 악성유저지만 여기서는 착하게 굴겠다. 사진은 경로이므로 띄어쓰기나 개행이 들어가면 안될 것 같다.")
                //.fileUrl("나는 악성유저지만 여기서는 착하게 굴겠다. 파일은 경로이므로 띄어쓰기나 개행이 들어가면 안될 것 같다.")
                .endDate(endDate7)
                .build();

        postsRepository.save(posts7);

        Category category7 = Category.builder()
                .posts(posts7)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category7.validateFieldCount();
        categoryRepository.save(category7);

        String initialEndDate8 = "2023-08-30";
        DateTimeFormatter dateFormatter8 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate8 = LocalDate.parse(initialEndDate8, dateFormatter8);

        Posts posts8 = Posts.builder()
                .user(user4)
                .postType(PostType.STUDY)
                .title("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.제목도길게쓴다.왜냐면난악성유저니까..." +
                        "제목도내맘대로두번이나쓴다ㅋㅋ.나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.제목도길게쓴다.왜냐면난악성유저니까...")
                .recruitmentCount(3)
                //.counts(1)
                .content("나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까..." +
                        "나는악성유저ㅋㅋㅋㅋ.띄어쓰기없다.일부러없앴다.사이트에오류를만들거다.아무도날막을수없다.내용도길게쓴다.왜냐면난악성유저니까...")
                //.promoteImageUrl("나는 악성유저지만 여기서는 착하게 굴겠다. 사진은 경로이므로 띄어쓰기나 개행이 들어가면 안될 것 같다.")
                //.fileUrl("나는 악성유저지만 여기서는 착하게 굴겠다. 파일은 경로이므로 띄어쓰기나 개행이 들어가면 안될 것 같다.")
                .endDate(endDate8)
                .build();

        postsRepository.save(posts8);

        Category category8 = Category.builder()
                .posts(posts8)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category8.validateFieldCount();
        categoryRepository.save(category8);


        String initialEndDate9 = "2023-11-20";
        DateTimeFormatter dateFormatter9 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate9 = LocalDate.parse(initialEndDate9, dateFormatter9);

        Posts posts9 = Posts.builder()
                .user(user4)
                .postType(PostType.STUDY)
                .title("악성유저의 정상적인 게시물ㅋㅋ")
                .recruitmentCount(3)
                //.counts(1)
                .content("웬일이래? 내가 정상적인 게시물도 달고 말이야. 고마워해라.")
                //.promoteImageUrl("")
                //.fileUrl("")
                .endDate(endDate9)
                .build();

        postsRepository.save(posts9);

        Category category9 = Category.builder()
                .posts(posts9)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category9.validateFieldCount();
        categoryRepository.save(category9);

        // 초기 데이터 생성 및 저장(5)
        User user5 = User.builder()
                .userName("홍길동")
                .nickName("고길동")
                .email("gogil@g.hongik.ac.kr")
                .password(passwordEncoder.encode("5"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 9, 25, 14, 30, 0))
                .imageUrl("profile18.png")
                .build();

        userRepository.save(user5);

        Portfolio user5Portfolio = Portfolio.builder()
                .user(user5)
                .web(0)
                .app(0)
                .game(4)
                .ai(3)
                .shortIntroduce("아버지를 아버지라 부르지 못하고..")
                .introduce("- 둘리 프로젝트 (주연) \n- 최초의 한글소설 프로젝트 (주연)")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user5Portfolio);

        String initialEndDate10 = "2023-11-20";
        DateTimeFormatter dateFormatter10 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate10 = LocalDate.parse(initialEndDate10, dateFormatter10);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts10 = Posts.builder()
                .user(user5)
                .postType(PostType.PROJECT)
                .title("게임이나 만들자.")
                .recruitmentCount(2)
                //.counts(1)
                .content("유니티, 언리얼 사용할 줄 아는 사람 환영.\nC# 잘 쓰고 C++ 잘하는 사람도 환영.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate10)
                .build();

        postsRepository.save(posts10);

        Category category10 = Category.builder()
                .posts(posts10)
                .web(false)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category10.validateFieldCount();
        categoryRepository.save(category10);

        String initialEndDate11 = "2023-12-01";
        DateTimeFormatter dateFormatter11 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate11 = LocalDate.parse(initialEndDate11, dateFormatter11);

        Posts posts11 = Posts.builder()
                .user(user5)
                .postType(PostType.PROJECT)
                .title("인공지능 마스터")
                .recruitmentCount(3)
                //.counts(1)
                .content("인공지능 잘 활용하시는 분과 함께 프로젝트 하고 싶어요.\n저와 함께 인공지능 마스터가 되어보아요!")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate11)
                .build();

        postsRepository.save(posts11);

        Category category11 = Category.builder()
                .posts(posts11)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category11.validateFieldCount();
        categoryRepository.save(category11);


        String initialEndDate12 = "2023-11-05";
        DateTimeFormatter dateFormatter12 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate12 = LocalDate.parse(initialEndDate12, dateFormatter12);

        Posts posts12 = Posts.builder()
                .user(user5)
                .postType(PostType.STUDY)
                .title("유니티랑 C# 같이 공부하실 분 구합니다.")
                .recruitmentCount(5)
                //.counts(1)
                .content("같이 열심히 공부해서, 플젝도 만들어봐요!!\n포트폴리오 열심히 채웁시다..")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate12)
                .build();

        postsRepository.save(posts12);

        Category category12 = Category.builder()
                .posts(posts12)
                .web(false)
                .app(false)
                .game(true)
                .ai(true)
                .build();

        category12.validateFieldCount();
        categoryRepository.save(category12);


        // 초기 데이터 생성 및 저장(6)
        User user6 = User.builder()
                .userName("성지민")
                .nickName("onlyProject")
                .email("opop@g.hongik.ac.kr")
                .password(passwordEncoder.encode("6"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 9, 2, 14, 30, 0))
                .imageUrl("profile3.png")
                .build();

        userRepository.save(user6);

        Portfolio user6Portfolio = Portfolio.builder()
                .user(user6)
                .web(4)
                .app(0)
                .game(0)
                .ai(0)
                .shortIntroduce("웹 장인")
                .introduce("- 스타트업 인턴 \n- 개인 토이 프로젝트 \n- 기타 등등")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user6Portfolio);

        String initialEndDate13 = "2023-12-19";
        DateTimeFormatter dateFormatter13 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate13 = LocalDate.parse(initialEndDate13, dateFormatter13);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts13 = Posts.builder()
                .user(user6)
                .postType(PostType.PROJECT)
                .title("프로젝트만 만들음.")
                .recruitmentCount(4)
                //.counts(1)
                .content("프로젝트만 모집할거임.\n스터디 모집 안함.\n내 맘임.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate13)
                .build();

        postsRepository.save(posts13);

        Category category13 = Category.builder()
                .posts(posts13)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category13.validateFieldCount();
        categoryRepository.save(category13);

        String initialEndDate14 = "2023-11-30";
        DateTimeFormatter dateFormatter14 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate14 = LocalDate.parse(initialEndDate14, dateFormatter14);

        Posts posts14 = Posts.builder()
                .user(user6)
                .postType(PostType.PROJECT)
                .title("인공지능 활용한 웹 개발")
                .recruitmentCount(3)
                //.counts(1)
                .content("자바 스프링 잘 쓰시는 분 구해요.\n인공지능 잘 활용하시는 분과 함께 프로젝트 하고 싶어요.\n저와 함께 웹 및 인공지능 마스터가 되어보아요!")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate14)
                .build();

        postsRepository.save(posts14);

        Category category14 = Category.builder()
                .posts(posts14)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category14.validateFieldCount();
        categoryRepository.save(category14);


        String initialEndDate15 = "2023-12-07";
        DateTimeFormatter dateFormatter15 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate15 = LocalDate.parse(initialEndDate15, dateFormatter15);

        Posts posts15 = Posts.builder()
                .user(user6)
                .postType(PostType.PROJECT)
                .title("제목 뭐로 하지..")
                .recruitmentCount(5)
                //.counts(1)
                .content("그냥 웹 플젝 할거야..\n포트폴리오 채워보자..")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate15)
                .build();

        postsRepository.save(posts15);

        Category category15 = Category.builder()
                .posts(posts15)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category15.validateFieldCount();
        categoryRepository.save(category15);


        // 초기 데이터 생성 및 저장(7)
        User user7 = User.builder()
                .userName("정인준")
                .nickName("injoon")
                .email("joon@g.hongik.ac.kr")
                .password(passwordEncoder.encode("7"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 9, 25, 14, 30, 0))
                .imageUrl("profile17.png")
                .build();

        userRepository.save(user7);

        Portfolio user7Portfolio = Portfolio.builder()
                .user(user7)
                .web(4)
                .app(0)
                .game(0)
                .ai(3)
                .shortIntroduce("시각디자인과에서의 경험을 바탕으로 미적 감각이 뛰어납니다. 하지만 백엔드를 희망합니다.")
                .introduce("- 맛있홍 프로젝트 (React + Node.js + Express.js \n- 픽미 프로젝트 (React + SpringBoot + JPA) \n- 졸업 프로젝트 (Unity)")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user7Portfolio);

        String initialEndDate19 = "2023-11-15"; // 원하는 종료 날짜를 스트링으로 받음
        DateTimeFormatter dateFormatter19 = DateTimeFormatter.ofPattern("yyyy-MM-dd");   // 날짜 포맷터를 사용하여 날짜 문자열을 'LocalDate' 개체로 변환
        LocalDate endDate19 = LocalDate.parse(initialEndDate19, dateFormatter19);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts19 = Posts.builder()
                .user(user7)
                .postType(PostType.PROJECT)
                .title("유니티 활용한 졸프")
                .recruitmentCount(2)
                //.counts(1)
                .content("유니티 잘 쓸 줄 아시는 분 두 분 구해봐용..\nC#도 잘하면 좋아요..")
                //.promoteImageUrl("사진 뭐하지")
                //.fileUrl("나도 몰라")
                .endDate(endDate19)
                .build();

        postsRepository.save(posts19);

        Category category19 = Category.builder()
                .posts(posts19)
                .web(false)
                .app(false)
                .game(true)
                .ai(true)
                .build();

        category19.validateFieldCount();
        categoryRepository.save(category19);

        String initialEndDate20 = "2023-12-28";
        DateTimeFormatter dateFormatter20 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate20 = LocalDate.parse(initialEndDate20, dateFormatter20);

        Posts posts20 = Posts.builder()
                .user(user7)
                .postType(PostType.STUDY)
                .title("알골 스터디 하실 분~")
                .recruitmentCount(4)
                //.counts(1)
                .content("알고리즘 스터디 구해여.\n매일 백준 한 문제씩 푸는 것이 목표에여.")
                //.promoteImageUrl("사진 없음")
                //.fileUrl("파일 없음")
                .endDate(endDate20)
                .build();

        postsRepository.save(posts20);

        Category category20 = Category.builder()
                .posts(posts20)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category20.validateFieldCount();
        categoryRepository.save(category20);


        String initialEndDate21 = "2023-11-20";
        DateTimeFormatter dateFormatter21 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate21 = LocalDate.parse(initialEndDate21, dateFormatter21);

        Posts posts21 = Posts.builder()
                .user(user7)
                .postType(PostType.STUDY)
                .title("송하윤 교수님의 길찾기 놀이")
                .recruitmentCount(5)
                //.counts(1)
                .content("두 길이 주어지면, 가운데 길을 예측해서 이어 보아요..\n지리에 관심있는 분 환영.\nAI에 관심있는 분 대 환영")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate21)
                .build();

        postsRepository.save(posts21);

        Category category21 = Category.builder()
                .posts(posts21)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category21.validateFieldCount();
        categoryRepository.save(category21);

        String initialEndDate22 = "2023-12-20";
        DateTimeFormatter dateFormatter22 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate22 = LocalDate.parse(initialEndDate21, dateFormatter22);

        Posts posts22 = Posts.builder()
                .user(user7)
                .postType(PostType.PROJECT)
                .title("웹 사이트 제작")
                .recruitmentCount(3)
                //.counts(1)
                .content("홍대 주변 맛집 사이트.\n미식에 관심있는 분 좋아요.\n코딩 잘하시는 분 좋아요.")
                //.promoteImageUrl("")
                //.fileUrl("")
                .endDate(endDate22)
                .build();

        postsRepository.save(posts22);

        Category category22 = Category.builder()
                .posts(posts22)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category22.validateFieldCount();
        categoryRepository.save(category22);


        // 초기 데이터 생성 및 저장(8)
        User user8 = User.builder()
                .userName("안채운")
                .nickName("onlyStudy")
                .email("osos@g.hongik.ac.kr")
                .password(passwordEncoder.encode("8"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 4, 27, 14, 30, 0))
                .imageUrl("profile4.png")
                .build();

        userRepository.save(user8);

        Portfolio user8Portfolio = Portfolio.builder()
                .user(user8)
                .web(0)
                .app(4)
                .game(0)
                .ai(0)
                .shortIntroduce("앱 전문가")
                .introduce("- 스타트업 인턴 \n- 학점 4.5 \n- 기타 등등")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user8Portfolio);

        String initialEndDate16 = "2023-11-11";
        DateTimeFormatter dateFormatter16 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate16 = LocalDate.parse(initialEndDate16, dateFormatter16);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts16 = Posts.builder()
                .user(user8)
                .postType(PostType.STUDY)
                .title("스터디만 만들음.")
                .recruitmentCount(4)
                //.counts(1)
                .content("스터디만 모집할거임.\n프로젝트 모집 안함.\n내 맘임.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate16)
                .build();

        postsRepository.save(posts16);

        Category category16 = Category.builder()
                .posts(posts16)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category16.validateFieldCount();
        categoryRepository.save(category16);

        String initialEndDate17 = "2023-12-03";
        DateTimeFormatter dateFormatter17 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate17 = LocalDate.parse(initialEndDate17, dateFormatter17);

        Posts posts17 = Posts.builder()
                .user(user8)
                .postType(PostType.STUDY)
                .title("코틀린 공부")
                .recruitmentCount(3)
                //.counts(1)
                .content("Kotlin 같이 공부해요.\n앱 처음 하시는 분들 저와 함께해요.\n열심히 해서 플젝도 같이 만들어봐요.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate17)
                .build();

        postsRepository.save(posts17);

        Category category17 = Category.builder()
                .posts(posts17)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category17.validateFieldCount();
        categoryRepository.save(category17);


        String initialEndDate18 = "2023-11-02";
        DateTimeFormatter dateFormatter18 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate18 = LocalDate.parse(initialEndDate18, dateFormatter18);

        Posts posts18 = Posts.builder()
                .user(user8)
                .postType(PostType.STUDY)
                .title("안드로이드? IOS?")
                .recruitmentCount(4)
                //.counts(1)
                .content("안드로이드에 관심있는 사람?\nIOS에 관심있는 사람?\n기초부터 차근차근 같이 공부해보자.\n자세한건 옵챗으로 얘기해요")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate18)
                .build();

        postsRepository.save(posts18);

        Category category18 = Category.builder()
                .posts(posts18)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category18.validateFieldCount();
        categoryRepository.save(category18);


        // 초기 데이터 생성 및 저장(9)
        User user9 = User.builder()
                .userName("윤지현")
                .nickName("jihyun")
                .email("jihyun@g.hongik.ac.kr")
                .password(passwordEncoder.encode("9"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile16.png")
                .build();

        userRepository.save(user9);

        Portfolio user9Portfolio = Portfolio.builder()
                .user(user9)
                .web(0)
                .app(0)
                .game(4)
                .ai(0)
                .shortIntroduce("게임 개발 장인")
                .introduce("- 대기업 개발자 \n- 학점 4.5 \n- 홍대 폰노이만 \n- 홍대 앨런 튜링")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user9Portfolio);

        String initialEndDate23 = "2023-12-12";
        DateTimeFormatter dateFormatter23 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate23 = LocalDate.parse(initialEndDate23, dateFormatter23);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts23 = Posts.builder()
                .user(user9)
                .postType(PostType.STUDY)
                .title("유니티 스터디 만들었어요.")
                .recruitmentCount(3)
                //.counts(1)
                .content("유니티 스터디.\nbox collider 2D를 아세요?\n모른다면 같이 스터디 ㄱㄱ.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate23)
                .build();

        postsRepository.save(posts23);

        Category category23 = Category.builder()
                .posts(posts23)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category23.validateFieldCount();
        categoryRepository.save(category23);

        String initialEndDate24 = "2023-11-10";
        DateTimeFormatter dateFormatter24 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate24 = LocalDate.parse(initialEndDate24, dateFormatter24);

        Posts posts24 = Posts.builder()
                .user(user9)
                .postType(PostType.STUDY)
                .title("언리얼 공부")
                .recruitmentCount(3)
                //.counts(1)
                .content("언리얼 기초부터 같이 공부하실분 구해요.\n저도 언리얼은 아무것도 몰라요.")
                //.promoteImageUrl(null)
                //.fileUrl(null)
                .endDate(endDate24)
                .build();

        postsRepository.save(posts24);

        Category category24 = Category.builder()
                .posts(posts24)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category24.validateFieldCount();
        categoryRepository.save(category24);


        String initialEndDate25 = "2023-11-16";
        DateTimeFormatter dateFormatter25 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate25 = LocalDate.parse(initialEndDate25, dateFormatter25);

        Posts posts25 = Posts.builder()
                .user(user9)
                .postType(PostType.PROJECT)
                .title("유니티로 앱 게임 만들기.")
                .recruitmentCount(2)
                //.counts(1)
                .content("유니티로 프로젝트 같이할 사람?\n기획, 사운드, 디자인까지 모두 모였음.\n잘하는 분만 모심.\n포트폴리오 볼거임\n너만 오면 바로 시작.")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate25)
                .build();

        postsRepository.save(posts25);

        Category category25 = Category.builder()
                .posts(posts25)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category25.validateFieldCount();
        categoryRepository.save(category25);


        String initialEndDate26 = "2023-12-12";
        DateTimeFormatter dateFormatter26 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate26 = LocalDate.parse(initialEndDate26, dateFormatter26);

        Posts posts26 = Posts.builder()
                .user(user9)
                .postType(PostType.PROJECT)
                .title("언리얼로 웹 게임 만들기.")
                .recruitmentCount(4)
                //.counts(1)
                .content("언리얼로 프로젝트 같이할 사람?\n기획, 사운드, 디자인까지 모두 모였음.\n잘하는 분만 모심.\n포트폴리오 볼거임\n너만 오면 바로 시작 예정.")
                //.promoteImageUrl("사진")
                //.fileUrl("파일")
                .endDate(endDate26)
                .build();

        postsRepository.save(posts26);

        Category category26 = Category.builder()
                .posts(posts26)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category26.validateFieldCount();
        categoryRepository.save(category26);






        // 초기 데이터 생성 및 저장(10)
        // ai 전문가 생성 예정
        // 게시물은 27부터 (유저 7, 9가 게시물이 4개임. 나머지는 3개.)







        // 추천 시스템 검증 전용 더미 데이터 만들기
        // 추천에 유효한 포폴까지만 생성하도록 하였음.
        // 0000 ~ 4321까지 총 209개의 더미 데이터 생성 필요
//
////        0000
//        createUserAndPortfolio("0000", "0000", "0000", "0000", 0, 0, 0, 0, "0000", "0000", "");
////        0001
//        createUserAndPortfolio("0001", "0001", "0001", "0001", 0, 0, 0, 1, "0001", "0001", "");
////        0002
//        createUserAndPortfolio("0002", "0002", "0002", "0002", 0, 0, 0, 2, "0002", "0002", "");
////        0003
//        createUserAndPortfolio("0003", "0003", "0003", "0003", 0, 0, 0, 3, "0003", "0003", "");
////        0004
//        createUserAndPortfolio("0004", "0004", "0004", "0004", 0, 0, 0, 4, "0004", "0004", "");
////        0010
//        createUserAndPortfolio("0010", "0010", "0010", "0010", 0, 0, 1, 0, "0010", "0010", "");
////        0012
//        createUserAndPortfolio("0012", "0012", "0012", "0012", 0, 0, 1, 2, "0012", "0012", "");
////        0013
//        createUserAndPortfolio("0013", "0013", "0013", "0013", 0, 0, 1, 3, "0013", "0013", "");
////        0014
//        createUserAndPortfolio("0014", "0014", "0014", "0014", 0, 0, 1, 4, "0014", "0014", "");
////        0020
//        createUserAndPortfolio("0020", "0020", "0020", "0020", 0, 0, 2, 0, "0020", "0020", "");
////        0021
//        createUserAndPortfolio("0021", "0021", "0021", "0021", 0, 0, 2, 1, "0021", "0021", "");
////        0023
//        createUserAndPortfolio("0023", "0023", "0023", "0023", 0, 0, 2, 3, "0023", "0023", "");
////        0024
//        createUserAndPortfolio("0024", "0024", "0024", "0024", 0, 0, 2, 4, "0024", "0024", "");
////        0030
//        createUserAndPortfolio("0030", "0030", "0030", "0030", 0, 0, 3, 0, "0030", "0030", "");
////        0031
//        createUserAndPortfolio("0031", "0031", "0031", "0031", 0, 0, 3, 1, "0031", "0031", "");
////        0032
//        createUserAndPortfolio("0032", "0032", "0032", "0032", 0, 0, 3, 2, "0032", "0032", "");
////        0034
//        createUserAndPortfolio("0034", "0034", "0034", "0034", 0, 0, 3, 4, "0034", "0034", "");
////        0040
//        createUserAndPortfolio("0040", "0040", "0040", "0040", 0, 0, 4, 0, "0040", "0040", "");
////        0041
//        createUserAndPortfolio("0041", "0041", "0041", "0041", 0, 0, 4, 1, "0041", "0041", "");
////        0042
//        createUserAndPortfolio("0042", "0042", "0042", "0042", 0, 0, 4, 2, "0042", "0042", "");
////        0043
//        createUserAndPortfolio("0043", "0043", "0043", "0043", 0, 0, 4, 3, "0043", "0043", "");
////        0100
//        createUserAndPortfolio("0100", "0100", "0100", "0100", 0, 1, 0, 0, "0100", "0100", "");
////        0102
//        createUserAndPortfolio("0102", "0102", "0102", "0102", 0, 1, 0, 2, "0102", "0102", "");
////        0103
//        createUserAndPortfolio("0103", "0103", "0103", "0103", 0, 1, 0, 3, "0103", "0103", "");
////        0104
//        createUserAndPortfolio("0104", "0104", "0104", "0104", 0, 1, 0, 4, "0104", "0104", "");
////        0120
//        createUserAndPortfolio("0120", "0120", "0120", "0120", 0, 1, 2, 0, "0120", "0120", "");
////        0123
//        createUserAndPortfolio("0123", "0123", "0123", "0123", 0, 1, 2, 3, "0123", "0123", "");
////        0124
//        createUserAndPortfolio("0124", "0124", "0124", "0124", 0, 1, 2, 4, "0124", "0124", "");
////        0130
//        createUserAndPortfolio("0130", "0130", "0130", "0130", 0, 1, 3, 0, "0130", "0130", "");
////        0132
//        createUserAndPortfolio("0132", "0132", "0132", "0132", 0, 1, 3, 2, "0132", "0132", "");
////        0134
//        createUserAndPortfolio("0134", "0134", "0134", "0134", 0, 1, 3, 4, "0134", "0134", "");
////        0140
//        createUserAndPortfolio("0140", "0140", "0140", "0140", 0, 1, 4, 0, "0140", "0140", "");
////        0142
//        createUserAndPortfolio("0142", "0142", "0142", "0142", 0, 1, 4, 2, "0142", "0142", "");
////        0143
//        createUserAndPortfolio("0143", "0143", "0143", "0143", 0, 1, 4, 3, "0143", "0143", "");
////        0200
//        createUserAndPortfolio("0200", "0200", "0200", "0200", 0, 2, 0, 0, "0200", "0200", "");
////        0201
//        createUserAndPortfolio("0201", "0201", "0201", "0201", 0, 2, 0, 1, "0201", "0201", "");
////        0203
//        createUserAndPortfolio("0203", "0203", "0203", "0203", 0, 2, 0, 3, "0203", "0203", "");
////        0204
//        createUserAndPortfolio("0204", "0204", "0204", "0204", 0, 2, 0, 4, "0204", "0204", "");
////        0210
//        createUserAndPortfolio("0210", "0210", "0210", "0210", 0, 2, 1, 0, "0210", "0210", "");
////        0213
//        createUserAndPortfolio("0213", "0213", "0213", "0213", 0, 2, 1, 3, "0213", "0213", "");
////        0214
//        createUserAndPortfolio("0214", "0214", "0214", "0214", 0, 2, 1, 4, "0214", "0214", "");
////        0230
//        createUserAndPortfolio("0230", "0230", "0230", "0230", 0, 2, 3, 0, "0230", "0230", "");
////        0231
//        createUserAndPortfolio("0231", "0231", "0231", "0231", 0, 2, 3, 1, "0231", "0231", "");
////        0234
//        createUserAndPortfolio("0234", "0234", "0234", "0234", 0, 2, 3, 4, "0234", "0234", "");
////        0240
//        createUserAndPortfolio("0240", "0240", "0240", "0240", 0, 2, 4, 0, "0240", "0240", "");
////        0241
//        createUserAndPortfolio("0241", "0241", "0241", "0241", 0, 2, 4, 1, "0241", "0241", "");
////        0243
//        createUserAndPortfolio("0243", "0243", "0243", "0243", 0, 2, 4, 3, "0243", "0243", "");
////        0300
//        createUserAndPortfolio("0300", "0300", "0300", "0300", 0, 3, 0, 0, "0300", "0300", "");
////        0301
//        createUserAndPortfolio("0301", "0301", "0301", "0301", 0, 3, 0, 1, "0301", "0301", "");
////        0302
//        createUserAndPortfolio("0302", "0302", "0302", "0302", 0, 3, 0, 2, "0302", "0302", "");
////        0304
//        createUserAndPortfolio("0304", "0304", "0304", "0304", 0, 3, 0, 4, "0304", "0304", "");
////        0310
//        createUserAndPortfolio("0310", "0310", "0310", "0310", 0, 3, 1, 0, "0310", "0310", "");
////        0312
//        createUserAndPortfolio("0312", "0312", "0312", "0312", 0, 3, 1, 2, "0312", "0312", "");
////        0314
//        createUserAndPortfolio("0314", "0314", "0314", "0314", 0, 3, 1, 4, "0314", "0314", "");
////        0320
//        createUserAndPortfolio("0320", "0320", "0320", "0320", 0, 3, 2, 0, "0320", "0320", "");
////        0321
//        createUserAndPortfolio("0321", "0321", "0321", "0321", 0, 3, 2, 1, "0321", "0321", "");
////        0324
//        createUserAndPortfolio("0324", "0324", "0324", "0324", 0, 3, 2, 4, "0324", "0324", "");
////        0340
//        createUserAndPortfolio("0340", "0340", "0340", "0340", 0, 3, 4, 0, "0340", "0340", "");
////        0341
//        createUserAndPortfolio("0341", "0341", "0341", "0341", 0, 3, 4, 1, "0341", "0341", "");
////        0342
//        createUserAndPortfolio("0342", "0342", "0342", "0342", 0, 3, 4, 2, "0342", "0342", "");
////        0400
//        createUserAndPortfolio("0400", "0400", "0400", "0400", 0, 4, 0, 0, "0400", "0400", "");
////        0401
//        createUserAndPortfolio("0401", "0401", "0401", "0401", 0, 4, 0, 1, "0401", "0401", "");
////        0402
//        createUserAndPortfolio("0402", "0402", "0402", "0402", 0, 4, 0, 2, "0402", "0402", "");
////        0403
//        createUserAndPortfolio("0403", "0403", "0403", "0403", 0, 4, 0, 3, "0403", "0403", "");
////        0410
//        createUserAndPortfolio("0410", "0410", "0410", "0410", 0, 4, 1, 0, "0410", "0410", "");
////        0412
//        createUserAndPortfolio("0412", "0412", "0412", "0412", 0, 4, 1, 2, "0412", "0412", "");
////        0413
//        createUserAndPortfolio("0413", "0413", "0413", "0413", 0, 4, 1, 3, "0413", "0413", "");
////        0420
//        createUserAndPortfolio("0420", "0420", "0420", "0420", 0, 4, 2, 0, "0420", "0420", "");
////        0421
//        createUserAndPortfolio("0421", "0421", "0421", "0421", 0, 4, 2, 1, "0421", "0421", "");
////        0423
//        createUserAndPortfolio("0423", "0423", "0423", "0423", 0, 4, 2, 3, "0423", "0423", "");
////        0430
//        createUserAndPortfolio("0430", "0430", "0430", "0430", 0, 4, 3, 0, "0430", "0430", "");
////        0431
//        createUserAndPortfolio("0431", "0431", "0431", "0431", 0, 4, 3, 1, "0431", "0431", "");
////        0432
//        createUserAndPortfolio("0432", "0432", "0432", "0432", 0, 4, 3, 2, "0432", "0432", "");
////        1000
//        createUserAndPortfolio("1000", "1000", "1000", "1000", 1, 0, 0, 0, "1000", "1000", "");
////        1002
//        createUserAndPortfolio("1002", "1002", "1002", "1002", 1, 0, 0, 2, "1002", "1002", "");
////        1003
//        createUserAndPortfolio("1003", "1003", "1003", "1003", 1, 0, 0, 3, "1003", "1003", "");
////        1004
//        createUserAndPortfolio("1004", "1004", "1004", "1004", 1, 0, 0, 4, "1004", "1004", "");
////        1020
//        createUserAndPortfolio("1020", "1020", "1020", "1020", 1, 0, 2, 0, "1020", "1020", "");
////        1023
//        createUserAndPortfolio("1023", "1023", "1023", "1023", 1, 0, 2, 3, "1023", "1023", "");
////        1024
//        createUserAndPortfolio("1024", "1024", "1024", "1024", 1, 0, 2, 4, "1024", "1024", "");
////        1030
//        createUserAndPortfolio("1030", "1030", "1030", "1030", 1, 0, 3, 0, "1030", "1030", "");
////        1032
//        createUserAndPortfolio("1032", "1032", "1032", "1032", 1, 0, 3, 2, "1032", "1032", "");
////        1034
//        createUserAndPortfolio("1034", "1034", "1034", "1034", 1, 0, 3, 4, "1034", "1034", "");
////        1040
//        createUserAndPortfolio("1040", "1040", "1040", "1040", 1, 0, 4, 0, "1040", "1040", "");
////        1042
//        createUserAndPortfolio("1042", "1042", "1042", "1042", 1, 0, 4, 2, "1042", "1042", "");
////        1043
//        createUserAndPortfolio("1043", "1043", "1043", "1043", 1, 0, 4, 3, "1043", "1043", "");
////        1200
//        createUserAndPortfolio("1200", "1200", "1200", "1200", 1, 2, 0, 0, "1200", "1200", "");
////        1203
//        createUserAndPortfolio("1203", "1203", "1203", "1203", 1, 2, 0, 3, "1203", "1203", "");
////        1204
//        createUserAndPortfolio("1204", "1204", "1204", "1204", 1, 2, 0, 4, "1204", "1204", "");
////        1230
//        createUserAndPortfolio("1230", "1230", "1230", "1230", 1, 2, 3, 0, "1230", "1230", "");
////        1234
//        createUserAndPortfolio("1234", "1234", "1234", "1234", 1, 2, 3, 4, "1234", "1234", "");
////        1240
//        createUserAndPortfolio("1240", "1240", "1240", "1240", 1, 2, 4, 0, "1240", "1240", "");
////        1243
//        createUserAndPortfolio("1243", "1243", "1243", "1243", 1, 2, 4, 3, "1243", "1243", "");
////        1300
//        createUserAndPortfolio("1300", "1300", "1300", "1300", 1, 3, 0, 0, "1300", "1300", "");
////        1302
//        createUserAndPortfolio("1302", "1302", "1302", "1302", 1, 3, 0, 2, "1302", "1302", "");
////        1304
//        createUserAndPortfolio("1304", "1304", "1304", "1304", 1, 3, 0, 4, "1304", "1304", "");
////        1320
//        createUserAndPortfolio("1320", "1320", "1320", "1320", 1, 3, 2, 0, "1320", "1320", "");
////        1324
//        createUserAndPortfolio("1324", "1324", "1324", "1324", 1, 3, 2, 4, "1324", "1324", "");
////        1340
//        createUserAndPortfolio("1340", "1340", "1340", "1340", 1, 3, 4, 0, "1340", "1340", "");
////        1342
//        createUserAndPortfolio("1342", "1342", "1342", "1342", 1, 3, 4, 2, "1342", "1342", "");
////        1400
//        createUserAndPortfolio("1400", "1400", "1400", "1400", 1, 4, 0, 0, "1400", "1400", "");
////        1402
//        createUserAndPortfolio("1402", "1402", "1402", "1402", 1, 4, 0, 2, "1402", "1402", "");
////        1403
//        createUserAndPortfolio("1403", "1403", "1403", "1403", 1, 4, 0, 3, "1403", "1403", "");
////        1420
//        createUserAndPortfolio("1420", "1420", "1420", "1420", 1, 4, 2, 0, "1420", "1420", "");
////        1423
//        createUserAndPortfolio("1423", "1423", "1423", "1423", 1, 4, 2, 3, "1423", "1423", "");
////        1430
//        createUserAndPortfolio("1430", "1430", "1430", "1430", 1, 4, 3, 0, "1430", "1430", "");
////        1432
//        createUserAndPortfolio("1432", "1432", "1432", "1432", 1, 4, 3, 2, "1432", "1432", "");
////        2000
//        createUserAndPortfolio("2000", "2000", "2000", "2000", 2, 0, 0, 0, "2000", "2000", "");
////        2001
//        createUserAndPortfolio("2001", "2001", "2001", "2001", 2, 0, 0, 1, "2001", "2001", "");
////        2003
//        createUserAndPortfolio("2003", "2003", "2003", "2003", 2, 0, 0, 3, "2003", "2003", "");
////        2004
//        createUserAndPortfolio("2004", "2004", "2004", "2004", 2, 0, 0, 4, "2004", "2004", "");
////        2010
//        createUserAndPortfolio("2010", "2010", "2010", "2010", 2, 0, 1, 0, "2010", "2010", "");
////        2013
//        createUserAndPortfolio("2013", "2013", "2013", "2013", 2, 0, 1, 3, "2013", "2013", "");
////        2014
//        createUserAndPortfolio("2014", "2014", "2014", "2014", 2, 0, 1, 4, "2014", "2014", "");
////        2030
//        createUserAndPortfolio("2030", "2030", "2030", "2030", 2, 0, 3, 0, "2030", "2030", "");
////        2031
//        createUserAndPortfolio("2031", "2031", "2031", "2031", 2, 0, 3, 1, "2031", "2031", "");
////        2034
//        createUserAndPortfolio("2034", "2034", "2034", "2034", 2, 0, 3, 4, "2034", "2034", "");
////        2040
//        createUserAndPortfolio("2040", "2040", "2040", "2040", 2, 0, 4, 0, "2040", "2040", "");
////        2041
//        createUserAndPortfolio("2041", "2041", "2041", "2041", 2, 0, 4, 1, "2041", "2041", "");
////        2043
//        createUserAndPortfolio("2043", "2043", "2043", "2043", 2, 0, 4, 3, "2043", "2043", "");
////        2100
//        createUserAndPortfolio("2100", "2100", "2100", "2100", 2, 1, 0, 0, "2100", "2100", "");
////        2103
//        createUserAndPortfolio("2103", "2103", "2103", "2103", 2, 1, 0, 3, "2103", "2103", "");
////        2104
//        createUserAndPortfolio("2104", "2104", "2104", "2104", 2, 1, 0, 4, "2104", "2104", "");
////        2130
//        createUserAndPortfolio("2130", "2130", "2130", "2130", 2, 1, 3, 0, "2130", "2130", "");
////        2134
//        createUserAndPortfolio("2134", "2134", "2134", "2134", 2, 1, 3, 4, "2134", "2134", "");
////        2140
//        createUserAndPortfolio("2140", "2140", "2140", "2140", 2, 1, 4, 0, "2140", "2140", "");
////        2143
//        createUserAndPortfolio("2143", "2143", "2143", "2143", 2, 1, 4, 3, "2143", "2143", "");
////        2300
//        createUserAndPortfolio("2300", "2300", "2300", "2300", 2, 3, 0, 0, "2300", "2300", "");
////        2301
//        createUserAndPortfolio("2301", "2301", "2301", "2301", 2, 3, 0, 1, "2301", "2301", "");
////        2304
//        createUserAndPortfolio("2304", "2304", "2304", "2304", 2, 3, 0, 4, "2304", "2304", "");
////        2310
//        createUserAndPortfolio("2310", "2310", "2310", "2310", 2, 3, 1, 0, "2310", "2310", "");
////        2314
//        createUserAndPortfolio("2314", "2314", "2314", "2314", 2, 3, 1, 4, "2314", "2314", "");
////        2340
//        createUserAndPortfolio("2340", "2340", "2340", "2340", 2, 3, 4, 0, "2340", "2340", "");
////        2341
//        createUserAndPortfolio("2341", "2341", "2341", "2341", 2, 3, 4, 1, "2341", "2341", "");
////        2400
//        createUserAndPortfolio("2400", "2400", "2400", "2400", 2, 4, 0, 0, "2400", "2400", "");
////        2401
//        createUserAndPortfolio("2401", "2401", "2401", "2401", 2, 4, 0, 1, "2401", "2401", "");
////        2403
//        createUserAndPortfolio("2403", "2403", "2403", "2403", 2, 4, 0, 3, "2403", "2403", "");
////        2410
//        createUserAndPortfolio("2410", "2410", "2410", "2410", 2, 4, 1, 0, "2410", "2410", "");
////        2413
//        createUserAndPortfolio("2413", "2413", "2413", "2413", 2, 4, 1, 3, "2413", "2413", "");
////        2430
//        createUserAndPortfolio("2430", "2430", "2430", "2430", 2, 4, 3, 0, "2430", "2430", "");
////        2431
//        createUserAndPortfolio("2431", "2431", "2431", "2431", 2, 4, 3, 1, "2431", "2431", "");
////        3000
//        createUserAndPortfolio("3000", "3000", "3000", "3000", 3, 0, 0, 0, "3000", "3000", "");
////        3001
//        createUserAndPortfolio("3001", "3001", "3001", "3001", 3, 0, 0, 1, "3001", "3001", "");
////        3002
//        createUserAndPortfolio("3002", "3002", "3002", "3002", 3, 0, 0, 2, "3002", "3002", "");
////        3004
//        createUserAndPortfolio("3004", "3004", "3004", "3004", 3, 0, 0, 4, "3004", "3004", "");
////        3010
//        createUserAndPortfolio("3010", "3010", "3010", "3010", 3, 0, 1, 0, "3010", "3010", "");
////        3012
//        createUserAndPortfolio("3012", "3012", "3012", "3012", 3, 0, 1, 2, "3012", "3012", "");
////        3014
//        createUserAndPortfolio("3014", "3014", "3014", "3014", 3, 0, 1, 4, "3014", "3014", "");
////        3020
//        createUserAndPortfolio("3020", "3020", "3020", "3020", 3, 0, 2, 0, "3020", "3020", "");
////        3021
//        createUserAndPortfolio("3021", "3021", "3021", "3021", 3, 0, 2, 1, "3021", "3021", "");
////        3024
//        createUserAndPortfolio("3024", "3024", "3024", "3024", 3, 0, 2, 4, "3024", "3024", "");
////        3040
//        createUserAndPortfolio("3040", "3040", "3040", "3040", 3, 0, 4, 0, "3040", "3040", "");
////        3041
//        createUserAndPortfolio("3041", "3041", "3041", "3041", 3, 0, 4, 1, "3041", "3041", "");
////        3042
//        createUserAndPortfolio("3042", "3042", "3042", "3042", 3, 0, 4, 2, "3042", "3042", "");
////        3100
//        createUserAndPortfolio("3100", "3100", "3100", "3100", 3, 1, 0, 0, "3100", "3100", "");
////        3102
//        createUserAndPortfolio("3102", "3102", "3102", "3102", 3, 1, 0, 2, "3102", "3102", "");
////        3104
//        createUserAndPortfolio("3104", "3104", "3104", "3104", 3, 1, 0, 4, "3104", "3104", "");
////        3120
//        createUserAndPortfolio("3120", "3120", "3120", "3120", 3, 1, 2, 0, "3120", "3120", "");
////        3124
//        createUserAndPortfolio("3124", "3124", "3124", "3124", 3, 1, 2, 4, "3124", "3124", "");
////        3140
//        createUserAndPortfolio("3140", "3140", "3140", "3140", 3, 1, 4, 0, "3140", "3140", "");
////        3142
//        createUserAndPortfolio("3142", "3142", "3142", "3142", 3, 1, 4, 2, "3142", "3142", "");
////        3200
//        createUserAndPortfolio("3200", "3200", "3200", "3200", 3, 2, 0, 0, "3200", "3200", "");
////        3201
//        createUserAndPortfolio("3201", "3201", "3201", "3201", 3, 2, 0, 1, "3201", "3201", "");
////        3204
//        createUserAndPortfolio("3204", "3204", "3204", "3204", 3, 2, 0, 4, "3204", "3204", "");
////        3210
//        createUserAndPortfolio("3210", "3210", "3210", "3210", 3, 2, 1, 0, "3210", "3210", "");
////        3214
//        createUserAndPortfolio("3214", "3214", "3214", "3214", 3, 2, 1, 4, "3214", "3214", "");
////        3240
//        createUserAndPortfolio("3240", "3240", "3240", "3240", 3, 2, 4, 0, "3240", "3240", "");
////        3241
//        createUserAndPortfolio("3241", "3241", "3241", "3241", 3, 2, 4, 1, "3241", "3241", "");
////        3400
//        createUserAndPortfolio("3400", "3400", "3400", "3400", 3, 4, 0, 0, "3400", "3400", "");
////        3401
//        createUserAndPortfolio("3401", "3401", "3401", "3401", 3, 4, 0, 1, "3401", "3401", "");
////        3402
//        createUserAndPortfolio("3402", "3402", "3402", "3402", 3, 4, 0, 2, "3402", "3402", "");
////        3410
//        createUserAndPortfolio("3410", "3410", "3410", "3410", 3, 4, 1, 0, "3410", "3410", "");
////        3412
//        createUserAndPortfolio("3412", "3412", "3412", "3412", 3, 4, 1, 2, "3412", "3412", "");
////        3420
//        createUserAndPortfolio("3420", "3420", "3420", "3420", 3, 4, 2, 0, "3420", "3420", "");
////        3421
//        createUserAndPortfolio("3421", "3421", "3421", "3421", 3, 4, 2, 1, "3421", "3421", "");
////        4000
//        createUserAndPortfolio("4000", "4000", "4000", "4000", 4, 0, 0, 0, "4000", "4000", "");
////        4001
//        createUserAndPortfolio("4001", "4001", "4001", "4001", 4, 0, 0, 1, "4001", "4001", "");
////        4002
//        createUserAndPortfolio("4002", "4002", "4002", "4002", 4, 0, 0, 2, "4002", "4002", "");
////        4003
//        createUserAndPortfolio("4003", "4003", "4003", "4003", 4, 0, 0, 3, "4003", "4003", "");
////        4010
//        createUserAndPortfolio("4010", "4010", "4010", "4010", 4, 0, 1, 0, "4010", "4010", "");
////        4012
//        createUserAndPortfolio("4012", "4012", "4012", "4012", 4, 0, 1, 2, "4012", "4012", "");
////        4013
//        createUserAndPortfolio("4013", "4013", "4013", "4013", 4, 0, 1, 3, "4013", "4013", "");
////        4020
//        createUserAndPortfolio("4020", "4020", "4020", "4020", 4, 0, 2, 0, "4020", "4020", "");
////        4021
//        createUserAndPortfolio("4021", "4021", "4021", "4021", 4, 0, 2, 1, "4021", "4021", "");
////        4023
//        createUserAndPortfolio("4023", "4023", "4023", "4023", 4, 0, 2, 3, "4023", "4023", "");
////        4030
//        createUserAndPortfolio("4030", "4030", "4030", "4030", 4, 0, 3, 0, "4030", "4030", "");
////        4031
//        createUserAndPortfolio("4031", "4031", "4031", "4031", 4, 0, 3, 1, "4031", "4031", "");
////        4032
//        createUserAndPortfolio("4032", "4032", "4032", "4032", 4, 0, 3, 2, "4032", "4032", "");
////        4100
//        createUserAndPortfolio("4100", "4100", "4100", "4100", 4, 1, 0, 0, "4100", "4100", "");
////        4102
//        createUserAndPortfolio("4102", "4102", "4102", "4102", 4, 1, 0, 2, "4102", "4102", "");
////        4103
//        createUserAndPortfolio("4103", "4103", "4103", "4103", 4, 1, 0, 3, "4103", "4103", "");
////        4120
//        createUserAndPortfolio("4120", "4120", "4120", "4120", 4, 1, 2, 0, "4120", "4120", "");
////        4123
//        createUserAndPortfolio("4123", "4123", "4123", "4123", 4, 1, 2, 3, "4123", "4123", "");
////        4130
//        createUserAndPortfolio("4130", "4130", "4130", "4130", 4, 1, 3, 0, "4130", "4130", "");
////        4132
//        createUserAndPortfolio("4132", "4132", "4132", "4132", 4, 1, 3, 2, "4132", "4132", "");
////        4200
//        createUserAndPortfolio("4200", "4200", "4200", "4200", 4, 2, 0, 0, "4200", "4200", "");
////        4201
//        createUserAndPortfolio("4201", "4201", "4201", "4201", 4, 2, 0, 1, "4201", "4201", "");
////        4203
//        createUserAndPortfolio("4203", "4203", "4203", "4203", 4, 2, 0, 3, "4203", "4203", "");
////        4210
//        createUserAndPortfolio("4210", "4210", "4210", "4210", 4, 2, 1, 0, "4210", "4210", "");
////        4213
//        createUserAndPortfolio("4213", "4213", "4213", "4213", 4, 2, 1, 3, "4213", "4213", "");
////        4230
//        createUserAndPortfolio("4230", "4230", "4230", "4230", 4, 2, 3, 0, "4230", "4230", "");
////        4231
//        createUserAndPortfolio("4231", "4231", "4231", "4231", 4, 2, 3, 1, "4231", "4231", "");
////        4300
//        createUserAndPortfolio("4300", "4300", "4300", "4300", 4, 3, 0, 0, "4300", "4300", "");
////        4301
//        createUserAndPortfolio("4301", "4301", "4301", "4301", 4, 3, 0, 1, "4301", "4301", "");
////        4302
//        createUserAndPortfolio("4302", "4302", "4302", "4302", 4, 3, 0, 2, "4302", "4302", "");
////        4310
//        createUserAndPortfolio("4310", "4310", "4310", "4310", 4, 3, 1, 0, "4310", "4310", "");
////        4312
//        createUserAndPortfolio("4312", "4312", "4312", "4312", 4, 3, 1, 2, "4312", "4312", "");
////        4320
//        createUserAndPortfolio("4320", "4320", "4320", "4320", 4, 3, 2, 0, "4320", "4320", "");
////        4321
//        createUserAndPortfolio("4321", "4321", "4321", "4321", 4, 3, 2, 1, "4321", "4321", "");


        // 초기 데이터 생성 및 저장(2)
        User user7000 = User.builder()
                .userName("최세영")
                .nickName("sangnamja")
                .email("csy626@g.hongik.ac.kr")
                .password(passwordEncoder.encode("626"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile4.png")
                .build();

        userRepository.save(user7000);

        Portfolio user7000Portfolio = Portfolio.builder()
                .user(user7000)
                .web(4)
                .app(3)
                .game(2)
                .ai(1)
                .shortIntroduce("카카오 네이버? 이런 IT 대기업보다는 전 스타트업에서 제 역량을 키우고 싶습니다.")
                .introduce("- 렉스, 야크, 프롤로그를 넘어서 콜그래프까지 과제를 제출\n" +
                        "- 학점 3.8\n" +
                        "- 정보처리기사 자격 보유")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user7000Portfolio);

        // 초기 데이터 생성 및 저장(2)
        User user7001 = User.builder()
                .userName("이두열")
                .nickName("홍익모비딕")
                .email("ldl515@g.hongik.ac.kr")
                .password(passwordEncoder.encode("515"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile15.png")
                .build();

        userRepository.save(user7001);

        Portfolio user7001Portfolio = Portfolio.builder()
                .user(user7001)
                .web(1)
                .app(2)
                .game(3)
                .ai(4)
                .shortIntroduce("기계과지만, 소프트웨어에 관심있는 4학년입니다.")
                .introduce("- 자율주행 동아리 활동, 입상 경력 보유(은상)\n" +
                        "- AI 부트캠프 활동 경험\n" +
                        "- 일반기계기사 자격 보유")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user7001Portfolio);

        // 초기 데이터 생성 및 저장(2)
        User user7002 = User.builder()
                .userName("노호수")
                .nickName("마라토니아")
                .email("hosoo313@g.hongik.ac.kr")
                .password(passwordEncoder.encode("313"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile6.png")
                .build();

        userRepository.save(user7002);

        Portfolio user7002Portfolio = Portfolio.builder()
                .user(user7002)
                .web(1)
                .app(2)
                .game(3)
                .ai(4)
                .shortIntroduce("현재 군대에서 복무하고 있지만, 내년에 복학하면 같이 열심히 성장할 학우분들을 모십니다.")
                .introduce("- NLP와 AI분야에 대해 학부연구생 활동 경험 보유\n" +
                        "- 동아리에서 알고리즘 강연 경험 보유\n" +
                        "- WEB 프로젝트 2개 경험")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user7002Portfolio);

        // 초기 데이터 생성 및 저장(2)
        User user7003 = User.builder()
                .userName("이선재")
                .nickName("언리얼장인")
                .email("sundae1226@g.hongik.ac.kr")
                .password(passwordEncoder.encode("1226"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile14.png")
                .build();

        userRepository.save(user7003);

        Portfolio user7003Portfolio = Portfolio.builder()
                .user(user7003)
                .web(1)
                .app(2)
                .game(4)
                .ai(3)
                .shortIntroduce("홍익대학교 컴퓨터공학과에 재학중인, 게임 개발에 관심있는 이선재입니다.")
                .introduce("- 게임 분야에 큰 관심이 있어, 유니티와 언리얼 관련해서 게임 제작 4건 경험 보유\n" +
                        "- 언리얼 같은 경우, 초중학생들 대상으로 학원강사 경력 있음.\n " +
                        "- 메타버스 관련 스터디에 관심 있음")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user7003Portfolio);




        // 초기 데이터 생성 및 저장(10)
        User user10 = User.builder()
                .userName("이윤식")
                .nickName("rilato")
                .email("leeyunsik@g.hongik.ac.kr")
                .password(passwordEncoder.encode("10"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile7.png")
                .build();

        userRepository.save(user10);

        Portfolio user10Portfolio = Portfolio.builder()
                .user(user10)
                .web(4)
                .app(0)
                .game(0)
                .ai(0)
                .shortIntroduce("안정적인 서버를 구축하는 백엔드 개발자")
                .introduce("- 대기업 백엔드 개발자 희망 \n- 프론트 개발자와 원활한 소통 가능할 정도의 React 지식 보유 \n- Node.JS, Spring Boot 사용 가능 \n- MongoDB, PostgreSQL 사용 가능 ")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user10Portfolio);

        // 초기 데이터 생성 및 저장(11)
        User user11 = User.builder()
                .userName("안기범")
                .nickName("AhnGiveUp")
                .email("gb1912@g.hongik.ac.kr")
                .password(passwordEncoder.encode("11"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile13.png")
                .build();

        userRepository.save(user11);

        Portfolio user11Portfolio = Portfolio.builder()
                .user(user11)
                .web(0)
                .app(0)
                .game(4)
                .ai(3)
                .shortIntroduce("게임을 좋아하는 남자")
                .introduce("- 대기업 종사자 \n- 학점 4.3 \n- 유니티를 활용하여 쳐라쳐라 매우쳐라 개발 \n- 언리얼을 활용하여 MMORPG 개발 중")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user11Portfolio);

        // 초기 데이터 생성 및 저장(12)
        User user12 = User.builder()
                .userName("노현준")
                .nickName("침놓는 티모")
                .email("rhj423@g.hongik.ac.kr")
                .password(passwordEncoder.encode("12"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile8.png")
                .build();

        userRepository.save(user12);

        Portfolio user12Portfolio = Portfolio.builder()
                .user(user12)
                .web(4)
                .app(0)
                .game(0)
                .ai(3)
                .shortIntroduce("한의원 웹 사이트 만들어드립니다.")
                .introduce("- 홍대 컴공 졸업 \n- 침 놓는 데에 관심 많음 \n- 침 맞는 것도 좋아함 \n- SpringBoot + TypeScript로 개발")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user12Portfolio);

        // 초기 데이터 생성 및 저장(13)
        User user13 = User.builder()
                .userName("박성대")
                .nickName("앱장인")
                .email("chumsungdae@g.hongik.ac.kr")
                .password(passwordEncoder.encode("13"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile12.png")
                .build();

        userRepository.save(user13);

        Portfolio user13Portfolio = Portfolio.builder()
                .user(user13)
                .web(0)
                .app(4)
                .game(0)
                .ai(3)
                .shortIntroduce("인공지능 활용한 어플리케이션 개발자")
                .introduce("- 의료 기기 관련 어플 개발자 \n- React Native 사용 \n- Kotlin 사용 가능 \n- 딥 러닝, 머신 러닝에 관한 지식 다량 함유")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user13Portfolio);


        // 초기 데이터 생성 및 저장(2)
        User user8000 = User.builder()
                .userName("김영희")
                .nickName("youngHee")
                .email("yh@g.hongik.ac.kr")
                .password(passwordEncoder.encode("younghee"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile9.png")
                .build();

        userRepository.save(user8000);

        Portfolio user8000Portfolio = Portfolio.builder()
                .user(user8000)
                .web(1)
                .app(0)
                .game(4)
                .ai(3)
                .shortIntroduce("컴공과 재간둥희 영희입니다!")
                .introduce("언리얼과 유니티를 주로 다룹니다. 함께 게임 제작하실 학우분 찾습니다!")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user8000Portfolio);

        // 초기 데이터 생성 및 저장(2)
        User user8001 = User.builder()
                .userName("이소라")
                .nickName("sora")
                .email("sora@g.hongik.ac.kr")
                .password(passwordEncoder.encode("sora"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile11.png")
                .build();

        userRepository.save(user8001);

        Portfolio user8001Portfolio = Portfolio.builder()
                .user(user8001)
                .web(1)
                .app(2)
                .game(0)
                .ai(0)
                .shortIntroduce("소라빵을 좋아하는 이소라입니다!")
                .introduce("앱, 웹 희망합니다! 하지만 아직 탐색중...이 전공 나랑 안맞는듯........")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user8001Portfolio);


        // 초기 데이터 생성 및 저장(2)
        User user8002 = User.builder()
                .userName("박나폴레옹")
                .nickName("kingpoleon")
                .email("kingpoleon@g.hongik.ac.kr")
                .password(passwordEncoder.encode("kingpoleon"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile10.png")
                .build();

        userRepository.save(user8002);

        Portfolio user8002Portfolio = Portfolio.builder()
                .user(user8002)
                .web(2)
                .app(1)
                .game(0)
                .ai(0)
                .shortIntroduce("프랑스인 아님, 유럽인 아님, 토종 한국인임")
                .introduce("요즘 케라스 공부중입니다. 매우 재미있습니다.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user8002Portfolio);

        // 초기 데이터 생성 및 저장(2)
        User user8003 = User.builder()
                .userName("최부자")
                .nickName("moneychoi")
                .email("moneychoi@g.hongi.ac.kr")
                .password(passwordEncoder.encode("moneychoi"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 8, 27, 14, 30, 0))
                .imageUrl("profile2.png")
                .build();

        userRepository.save(user8003);

        Portfolio user8003Portfolio = Portfolio.builder()
                .user(user8003)
                .web(1)
                .app(2)
                .game(0)
                .ai(4)
                .shortIntroduce("돈 좋아합니다 ㅋㅋ 같이 입상해서 돈 받아요")
                .introduce("같이 공모전 나가서 상금 500만원 받을 팀원 모집합니다. 성실했으면 좋겠어요")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user8003Portfolio);




        // 초기 데이터 생성 및 저장 (윤식)
        // 모바일 게임
        User user100 = User.builder()
                .userName("김홍익")
                .nickName("khi")
                .email("khi@g.hongik.ac.kr")
                .password(passwordEncoder.encode("100"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile21.jpg")
                .build();

        userRepository.save(user100);

        Portfolio user100Portfolio = Portfolio.builder()
                .user(user100)
                .web(0)
                .app(3)
                .game(4)
                .ai(0)
                .shortIntroduce("유니티가 좋아요~")
                .introduce("- 유니티와 C# 많이 다루어봤어요 \n- 쳐라쳐라 매우쳐라 게임 개발 \n- 3Cards 게임 개발")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user100Portfolio);

        initialEndDate100 = "2024-01-02";
        dateFormatter100 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        endDate100 = LocalDate.parse(initialEndDate100, dateFormatter100);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts100 = Posts.builder()
                .user(user100)
                .postType(PostType.STUDY)
                .title("저랑 같이 C# 알고리즘 공부하실분~")
                .recruitmentCount(3)
                .content("C#으로 코테 준비해요.\n초보도 환영\n백준 브론즈부터 시작할 예정이에요.")
                .endDate(endDate100)
                .build();

        postsRepository.save(posts100);

        Category category100 = Category.builder()
                .posts(posts100)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category100.validateFieldCount();
        categoryRepository.save(category100);

        String initialEndDate101 = "2024-01-10";
        DateTimeFormatter dateFormatter101 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate101 = LocalDate.parse(initialEndDate101, dateFormatter101);

        Posts posts101 = Posts.builder()
                .user(user100)
                .postType(PostType.PROJECT)
                .title("모바일 웹 게임 같이 만들어요~")
                .recruitmentCount(4)
                .content("언리얼 기초부터 같이 공부하실분 구해요.\n저도 언리얼은 아무것도 몰라요.")
                .endDate(endDate101)
                .build();

        postsRepository.save(posts101);

        Category category101 = Category.builder()
                .posts(posts101)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category101.validateFieldCount();
        categoryRepository.save(category101);



        // 모바일 게임
        User user102 = User.builder()
                .userName("이폴가")
                .nickName("폴가이즈")
                .email("fallguys@g.hongik.ac.kr")
                .password(passwordEncoder.encode("102"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile22.jpg")
                .build();

        userRepository.save(user102);

        Portfolio user102Portfolio = Portfolio.builder()
                .user(user102)
                .web(0)
                .app(3)
                .game(4)
                .ai(0)
                .shortIntroduce("유니티가 좋아요~")
                .introduce("- 유니티와 C# 많이 다루어봤어요 \n- 쳐라쳐라 매우쳐라 게임 개발 \n- 3Cards 게임 개발")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user100Portfolio);

        String initialEndDate102 = "2024-01-02";
        DateTimeFormatter dateFormatter102 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate102 = LocalDate.parse(initialEndDate102, dateFormatter102);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts102 = Posts.builder()
                .user(user102)
                .postType(PostType.STUDY)
                .title("유니티 노베 같이 공부할 사람 구해용")
                .recruitmentCount(4)
                .content("유니티로 앱 게임 만들고 싶어요..\n근데 아직 유니티 공부 안해봣어요ㅠ\n저랑 같이 공부해요!!")
                .endDate(endDate102)
                .build();

        postsRepository.save(posts102);

        Category category102 = Category.builder()
                .posts(posts102)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category102.validateFieldCount();
        categoryRepository.save(category102);

        String initialEndDate103 = "2024-01-17";
        DateTimeFormatter dateFormatter103 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate103 = LocalDate.parse(initialEndDate103, dateFormatter103);

        Posts posts103 = Posts.builder()
                .user(user102)
                .postType(PostType.PROJECT)
                .title("모바일 웹 게임 같이 만들어요~")
                .recruitmentCount(4)
                .content("언리얼 기초부터 같이 공부하실분 구해요.\n저도 언리얼은 아무것도 몰라요.")
                .endDate(endDate103)
                .build();

        postsRepository.save(posts103);

        Category category103 = Category.builder()
                .posts(posts103)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category103.validateFieldCount();
        categoryRepository.save(category103);




        // 웹
        User user104 = User.builder()
                .userName("박지락")
                .nickName("봉골레러버")
                .email("lovepasta@g.hongik.ac.kr")
                .password(passwordEncoder.encode("104"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile23.jpg")
                .build();

        userRepository.save(user104);

        Portfolio user104Portfolio = Portfolio.builder()
                .user(user104)
                .web(0)
                .app(0)
                .game(4)
                .ai(0)
                .shortIntroduce("백엔드 장인")
                .introduce("- 각종 프로젝트 백엔드 다수 경험 \n- 궁금해? \n- 궁금하면 나한테 메일로..")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user104Portfolio);

        String initialEndDate104 = "2024-02-22";
        DateTimeFormatter dateFormatter104 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate104 = LocalDate.parse(initialEndDate104, dateFormatter104);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts104 = Posts.builder()
                .user(user104)
                .postType(PostType.STUDY)
                .title("스프링 시큐리티 공부하실 분")
                .recruitmentCount(4)
                .content("백엔드 보안과 관련하여 공부합시다.\n잘 다루시는 분 환영\n못해도 괜찮음")
                .endDate(endDate104)
                .build();

        postsRepository.save(posts104);

        Category category104 = Category.builder()
                .posts(posts104)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category104.validateFieldCount();
        categoryRepository.save(category104);

        String initialEndDate105 = "2024-02-07";
        DateTimeFormatter dateFormatter105 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate105 = LocalDate.parse(initialEndDate105, dateFormatter105);

        Posts posts105 = Posts.builder()
                .user(user104)
                .postType(PostType.PROJECT)
                .title("스프링 시큐리티 활용한 웹 프로젝트 개발")
                .recruitmentCount(5)
                .content("스프링 시큐리티 잘 다루시는 백엔드 2명 구합니다.\nVue 잘 다루는 프론트엔드 2명 구합니다.")
                .endDate(endDate105)
                .build();

        postsRepository.save(posts105);

        Category category105 = Category.builder()
                .posts(posts105)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category105.validateFieldCount();
        categoryRepository.save(category105);




        // 앱
        User user106 = User.builder()
                .userName("최사원")
                .nickName("바다가좋아")
                .email("temple@g.hongik.ac.kr")
                .password(passwordEncoder.encode("106"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile24.jpg")
                .build();

        userRepository.save(user106);

        Portfolio user106Portfolio = Portfolio.builder()
                .user(user106)
                .web(0)
                .app(4)
                .game(0)
                .ai(0)
                .shortIntroduce("앱 개발 전문가")
                .introduce("- 앱 서버 구축 다수 경험 \n- 스프링 부트 사용 \n- 함께 플젝하실 프론트 찾아요~")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user106Portfolio);

        String initialEndDate106 = "2024-03-10";
        DateTimeFormatter dateFormatter106 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate106 = LocalDate.parse(initialEndDate106, dateFormatter106);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts106 = Posts.builder()
                .user(user106)
                .postType(PostType.STUDY)
                .title("React Native")
                .recruitmentCount(3)
                .content("리액트 네이티브 스터디\n프론트는 저도 처음이에요\n")
                .endDate(endDate106)
                .build();

        postsRepository.save(posts106);

        Category category106 = Category.builder()
                .posts(posts106)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category106.validateFieldCount();
        categoryRepository.save(category106);

        String initialEndDate107 = "2024-01-06";
        DateTimeFormatter dateFormatter107 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate107 = LocalDate.parse(initialEndDate107, dateFormatter107);

        Posts posts107 = Posts.builder()
                .user(user106)
                .postType(PostType.PROJECT)
                .title("JWT 써서 로그인 구현한 웹")
                .recruitmentCount(4)
                .content("node js, express 사용합니다.\n 백엔드 1명 구합니다.\nJavaScript 쓸 줄 아시는 프론트엔드 2명 구합니다.")
                .endDate(endDate107)
                .build();

        postsRepository.save(posts107);

        Category category107 = Category.builder()
                .posts(posts107)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category107.validateFieldCount();
        categoryRepository.save(category107);




        // 웹
        User user108 = User.builder()
                .userName("강해운")
                .nickName("해운대")
                .email("haeundae@g.hongik.ac.kr")
                .password(passwordEncoder.encode("108"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile25.jpg")
                .build();

        userRepository.save(user108);

        Portfolio user108Portfolio = Portfolio.builder()
                .user(user108)
                .web(4)
                .app(0)
                .game(0)
                .ai(0)
                .shortIntroduce("자바스크립트 전문가")
                .introduce("- 프론트엔드 구축 다수 경험 \n- 자바스크립트 사용 \n- 함께 플젝하실 백 찾아요~")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user108Portfolio);

        String initialEndDate108 = "2024-02-11";
        DateTimeFormatter dateFormatter108 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate108 = LocalDate.parse(initialEndDate108, dateFormatter108);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts108 = Posts.builder()
                .user(user108)
                .postType(PostType.STUDY)
                .title("자스자스")
                .recruitmentCount(4)
                .content("자바스크립트 코테 준비\n프론트 하시는 분들은 저와 함께 스터디하시면 도움 많이 될 거에요!!\n")
                .endDate(endDate108)
                .build();

        postsRepository.save(posts108);

        Category category108 = Category.builder()
                .posts(posts108)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category108.validateFieldCount();
        categoryRepository.save(category108);

        String initialEndDate109 = "2024-01-16";
        DateTimeFormatter dateFormatter109 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate109 = LocalDate.parse(initialEndDate109, dateFormatter109);

        Posts posts109 = Posts.builder()
                .user(user108)
                .postType(PostType.PROJECT)
                .title("프론트는 자바스크립트 사용. 프로젝트 같이 할 백엔드 개발자 구해요~")
                .recruitmentCount(3)
                .content("자바스크립트 사용합니다.\n백엔드 1명 구합니다.\n자바스크립트 장인 프론트엔드도 1명 구합니다.")
                .endDate(endDate109)
                .build();

        postsRepository.save(posts109);

        Category category109 = Category.builder()
                .posts(posts109)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category109.validateFieldCount();
        categoryRepository.save(category109);




        // 웹, AI
        User user110 = User.builder()
                .userName("나하늘")
                .nickName("밤하늘의풍경")
                .email("sky@g.hongik.ac.kr")
                .password(passwordEncoder.encode("110"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile26.jpg")
                .build();

        userRepository.save(user110);

        Portfolio user110Portfolio = Portfolio.builder()
                .user(user110)
                .web(4)
                .app(0)
                .game(0)
                .ai(3)
                .shortIntroduce("텐서플로우를 활용한 딥러닝 마스터")
                .introduce("- 파이썬 사용합니다. \n- 장고를 활용하여 백엔드 구축합니다. \n- 딥러닝 많이 해봤어요")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user110Portfolio);

        String initialEndDate110 = "2024-03-03";
        DateTimeFormatter dateFormatter110 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate110 = LocalDate.parse(initialEndDate110, dateFormatter110);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts110 = Posts.builder()
                .user(user110)
                .postType(PostType.STUDY)
                .title("텐서플로")
                .recruitmentCount(3)
                .content("AI 처음 접하시는 분 계신가요?\n제가 친절하게 알려드립니다.\n같이 파이썬 공부해요~")
                .endDate(endDate110)
                .build();

        postsRepository.save(posts110);

        Category category110 = Category.builder()
                .posts(posts110)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category110.validateFieldCount();
        categoryRepository.save(category110);

        String initialEndDate111 = "2024-01-11";
        DateTimeFormatter dateFormatter111 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate111 = LocalDate.parse(initialEndDate111, dateFormatter111);

        Posts posts111 = Posts.builder()
                .user(user110)
                .postType(PostType.PROJECT)
                .title("Tensorflow를 활용한 서버 개발")
                .recruitmentCount(3)
                .content("딥러닝을 활용한 토이 플젝하실 분 구합니다.\n백엔드 2명 구해요.\n코딩 감자도 환영.")
                .endDate(endDate111)
                .build();

        postsRepository.save(posts111);

        Category category111 = Category.builder()
                .posts(posts111)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category111.validateFieldCount();
        categoryRepository.save(category111);




        // 앱, AI
        User user112 = User.builder()
                .userName("문사막")
                .nickName("이카사막")
                .email("desert@g.hongik.ac.kr")
                .password(passwordEncoder.encode("112"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile27.jpg")
                .build();

        userRepository.save(user112);

        Portfolio user112Portfolio = Portfolio.builder()
                .user(user112)
                .web(0)
                .app(4)
                .game(0)
                .ai(3)
                .shortIntroduce("파이토치 쓸 줄 아는 백엔드 개발자")
                .introduce("- 파이썬이 주력 언어입니다. \n- 장고로 백엔드 구축합니다. \n- 파이토치 이제 막 배우기 시작했어요")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user112Portfolio);

        String initialEndDate112 = "2024-01-01";
        DateTimeFormatter dateFormatter112 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate112 = LocalDate.parse(initialEndDate112, dateFormatter112);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts112 = Posts.builder()
                .user(user112)
                .postType(PostType.STUDY)
                .title("파이토치")
                .recruitmentCount(3)
                .content("파이토치 개초보와 함깨 하는 파이토치 공부\n잘 하는 분 환영\n못하는 분도 환영")
                .endDate(endDate112)
                .build();

        postsRepository.save(posts112);

        Category category112 = Category.builder()
                .posts(posts112)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category112.validateFieldCount();
        categoryRepository.save(category112);

        String initialEndDate113 = "2024-01-21";
        DateTimeFormatter dateFormatter113 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate113 = LocalDate.parse(initialEndDate113, dateFormatter113);

        Posts posts113 = Posts.builder()
                .user(user112)
                .postType(PostType.PROJECT)
                .title("파이토치를 활용한 서버 개발")
                .recruitmentCount(4)
                .content("파이토치를 사용한 딥러닝으로 졸프 같이 하실 분 구합니다.\n파이토치 쓰시는 백엔드 개발자 1명, React Native쓰시는 프론트엔드 개발자 2명 구해요.\n")
                .endDate(endDate113)
                .build();

        postsRepository.save(posts113);

        Category category113 = Category.builder()
                .posts(posts113)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category113.validateFieldCount();
        categoryRepository.save(category113);




        // 게임, 앱, AI
        User user114 = User.builder()
                .userName("배국밥")
                .nickName("수운대애구욱")
                .email("gukbabchoong@g.hongik.ac.kr")
                .password(passwordEncoder.encode("114"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile28.jpg")
                .build();

        userRepository.save(user114);

        Portfolio user114Portfolio = Portfolio.builder()
                .user(user114)
                .web(0)
                .app(3)
                .game(4)
                .ai(2)
                .shortIntroduce("모바일 게임 개발자")
                .introduce("- 유니티로 모바일 게임 제작합니다. \n- AI에는 조금 관심 있어요. \n- 파이토치나 텐서플로 둘 다 관심 있어요.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user114Portfolio);

        String initialEndDate114 = "2024-01-01";
        DateTimeFormatter dateFormatter114 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate114 = LocalDate.parse(initialEndDate114, dateFormatter114);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts114 = Posts.builder()
                .user(user114)
                .postType(PostType.STUDY)
                .title("Unity 공부 같이 해여")
                .recruitmentCount(2)
                .content("Unity 스터디 같이 해보고, 마음 맞으면 졸프도 같이 하실 분 괌\n")
                .endDate(endDate114)
                .build();

        postsRepository.save(posts114);

        Category category114 = Category.builder()
                .posts(posts114)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category114.validateFieldCount();
        categoryRepository.save(category114);

        String initialEndDate115 = "2024-01-21";
        DateTimeFormatter dateFormatter115 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate115 = LocalDate.parse(initialEndDate115, dateFormatter115);

        Posts posts115 = Posts.builder()
                .user(user114)
                .postType(PostType.PROJECT)
                .title("디펜스 게임 만들어여")
                .recruitmentCount(5)
                .content("인공지능을 활용한 모바일 디펜스 게임 구상 완료됐어요.\n이미 기획, 사운드, 그래픽 모두 모였고, 개발자만 더 구해봅니다..\n")
                .endDate(endDate115)
                .build();

        postsRepository.save(posts115);

        Category category115 = Category.builder()
                .posts(posts115)
                .web(false)
                .app(false)
                .game(true)
                .ai(true)
                .build();

        category115.validateFieldCount();
        categoryRepository.save(category115);




        // 게임, 앱
        User user116 = User.builder()
                .userName("Debruyne")
                .nickName("Foreigner")
                .email("foreigner@g.hongik.ac.kr")
                .password(passwordEncoder.encode("116"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile29.jpg")
                .build();

        userRepository.save(user116);

        Portfolio user116Portfolio = Portfolio.builder()
                .user(user116)
                .web(0)
                .app(3)
                .game(4)
                .ai(0)
                .shortIntroduce("Mobile Game Planner")
                .introduce("- I'm a Mobile Game Planner \n- I'm looking for a Back/Front Developer \n")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user116Portfolio);

        String initialEndDate116 = "2024-02-27";
        DateTimeFormatter dateFormatter116 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate116 = LocalDate.parse(initialEndDate116, dateFormatter116);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts116 = Posts.builder()
                .user(user116)
                .postType(PostType.STUDY)
                .title("Let's study how to make game")
                .recruitmentCount(3)
                .content("I'm a mobile game planner\nIf you are interested in mobile game planning, let's study together.")
                .endDate(endDate116)
                .build();

        postsRepository.save(posts116);

        Category category116 = Category.builder()
                .posts(posts116)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category116.validateFieldCount();
        categoryRepository.save(category116);

        String initialEndDate117 = "2024-01-19";
        DateTimeFormatter dateFormatter117 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate117 = LocalDate.parse(initialEndDate117, dateFormatter117);

        Posts posts117 = Posts.builder()
                .user(user116)
                .postType(PostType.PROJECT)
                .title("Let's make an app game")
                .recruitmentCount(3)
                .content("I'm a mobile game planner\nIf you are interested in developing mobile games, let's make an app game together.\n")
                .endDate(endDate117)
                .build();

        postsRepository.save(posts117);

        Category category117 = Category.builder()
                .posts(posts117)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category117.validateFieldCount();
        categoryRepository.save(category117);




        // 웹
        User user118 = User.builder()
                .userName("kevin")
                .nickName("Airplane")
                .email("airplane@g.hongik.ac.kr")
                .password(passwordEncoder.encode("118"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile30.jpg")
                .build();

        userRepository.save(user118);

        Portfolio user118Portfolio = Portfolio.builder()
                .user(user118)
                .web(4)
                .app(0)
                .game(0)
                .ai(0)
                .shortIntroduce("Web Service Developer")
                .introduce("- Hi there!\n- I'm a Web Service Developer \n- I'm interested in developing a backend using a spring boot.\n And I'm also interested in developing a backend using a node js.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user118Portfolio);

        String initialEndDate118 = "2024-01-22";
        DateTimeFormatter dateFormatter118 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate118 = LocalDate.parse(initialEndDate118, dateFormatter118);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts118 = Posts.builder()
                .user(user118)
                .postType(PostType.STUDY)
                .title("Let's study Spring Boot")
                .recruitmentCount(4)
                .content("I'm a Web Service Developer\nIf you are interested in developing a backend server, Join us!!")
                .endDate(endDate118)
                .build();

        postsRepository.save(posts118);

        Category category118 = Category.builder()
                .posts(posts118)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category118.validateFieldCount();
        categoryRepository.save(category118);

        String initialEndDate119 = "2024-01-06";
        DateTimeFormatter dateFormatter119 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate119 = LocalDate.parse(initialEndDate119, dateFormatter119);

        Posts posts119 = Posts.builder()
                .user(user118)
                .postType(PostType.PROJECT)
                .title("Let's make an app game")
                .recruitmentCount(4)
                .content("I'm a Web Service Developer\nLet's make a web using react and spring boot!\n")
                .endDate(endDate119)
                .build();

        postsRepository.save(posts119);

        Category category119 = Category.builder()
                .posts(posts119)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category119.validateFieldCount();
        categoryRepository.save(category119);




        // 앱
        User user120 = User.builder()
                .userName("고한강")
                .nickName("HanRiver")
                .email("hanriver@g.hongik.ac.kr")
                .password(passwordEncoder.encode("120"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile31.jpg")
                .build();

        userRepository.save(user120);

        Portfolio user120Portfolio = Portfolio.builder()
                .user(user120)
                .web(0)
                .app(4)
                .game(0)
                .ai(0)
                .shortIntroduce("모바일 앱 개발자")
                .introduce("- 안녕하세요!\n- 모바일 어플리케이션 개발자, 고한강입니다.\n- 아직은 개발이 서툴지만, 차근차근 배우고 있어요.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user120Portfolio);

        String initialEndDate120 = "2024-01-09";
        DateTimeFormatter dateFormatter120 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate120 = LocalDate.parse(initialEndDate120, dateFormatter120);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts120 = Posts.builder()
                .user(user120)
                .postType(PostType.STUDY)
                .title("앱개발 초짜")
                .recruitmentCount(4)
                .content("앱 개발 처음 공부하는데..\n어디서부터 어떻게 해야할 지 막막하네요..\n같이 스터디하실 분 계신가요??")
                .endDate(endDate120)
                .build();

        postsRepository.save(posts120);

        Category category120 = Category.builder()
                .posts(posts120)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category120.validateFieldCount();
        categoryRepository.save(category120);

        String initialEndDate121 = "2024-01-06";
        DateTimeFormatter dateFormatter121 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate121 = LocalDate.parse(initialEndDate121, dateFormatter121);

        Posts posts121 = Posts.builder()
                .user(user120)
                .postType(PostType.PROJECT)
                .title("앱 프로젝트 만드실 분~")
                .recruitmentCount(4)
                .content("졸프 같이할 사람 찾아요\n아직 개발이 서툴다면, 스터디 먼저 같이 해봐요!\n")
                .endDate(endDate121)
                .build();

        postsRepository.save(posts121);

        Category category121 = Category.builder()
                .posts(posts121)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category121.validateFieldCount();
        categoryRepository.save(category121);




        // 앱
        User user122 = User.builder()
                .userName("장팔")
                .nickName("팔이긴사나이")
                .email("longarm@g.hongik.ac.kr")
                .password(passwordEncoder.encode("122"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile32.jpg")
                .build();

        userRepository.save(user122);

        Portfolio user122Portfolio = Portfolio.builder()
                .user(user122)
                .web(0)
                .app(4)
                .game(0)
                .ai(0)
                .shortIntroduce("앱등이")
                .introduce("- 앱 개발 좋아!\n- 애플 좋아!\n- 깔끔한 디자인!")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user122Portfolio);

        String initialEndDate122 = "2024-02-19";
        DateTimeFormatter dateFormatter122 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate122 = LocalDate.parse(initialEndDate122, dateFormatter122);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts122 = Posts.builder()
                .user(user122)
                .postType(PostType.STUDY)
                .title("애플 디자인 공부")
                .recruitmentCount(4)
                .content("애플처럼 깔끔한 디자인 만드는 거 공부하실 분\n프론트엔드 개발자들 환영\n디자인 관심있는 분 모두 환영")
                .endDate(endDate122)
                .build();

        postsRepository.save(posts122);

        Category category122 = Category.builder()
                .posts(posts122)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category122.validateFieldCount();
        categoryRepository.save(category122);

        String initialEndDate123 = "2024-03-03";
        DateTimeFormatter dateFormatter123 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate123 = LocalDate.parse(initialEndDate123, dateFormatter123);

        Posts posts123 = Posts.builder()
                .user(user122)
                .postType(PostType.PROJECT)
                .title("애플처럼 깔끔한 디자인으로 프론트 구축합니다.")
                .recruitmentCount(3)
                .content("프로젝트는 저 혼자 독박쓸거에요\n스프링 부트, 노드, 익스프레스 등 백엔드 개발자 2명 구합니다.\n님만 오면 바로 시작.")
                .endDate(endDate123)
                .build();

        postsRepository.save(posts123);

        Category category123 = Category.builder()
                .posts(posts123)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category123.validateFieldCount();
        categoryRepository.save(category123);




        // 게임, 웹
        User user124 = User.builder()
                .userName("정야근")
                .nickName("혼모노웹개발자")
                .email("honmono@g.hongik.ac.kr")
                .password(passwordEncoder.encode("124"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile33.jpg")
                .build();

        userRepository.save(user124);

        Portfolio user124Portfolio = Portfolio.builder()
                .user(user124)
                .web(3)
                .app(0)
                .game(4)
                .ai(0)
                .shortIntroduce("게임둥이")
                .introduce("- 언리얼 원툴!\n- 유니티 몰라!\n- 웹 게임 만들자!")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user124Portfolio);

        String initialEndDate124 = "2024-03-29";
        DateTimeFormatter dateFormatter124 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate124 = LocalDate.parse(initialEndDate124, dateFormatter124);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts124 = Posts.builder()
                .user(user124)
                .postType(PostType.STUDY)
                .title("언리얼 웹 스터디")
                .recruitmentCount(3)
                .content("언리얼 배울 곳이 마땅히 없네요\n동아리도 없는거 같고..\n저랑 으쌰으쌰 해봐요")
                .endDate(endDate124)
                .build();

        postsRepository.save(posts124);

        Category category124 = Category.builder()
                .posts(posts124)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category124.validateFieldCount();
        categoryRepository.save(category124);

        String initialEndDate125 = "2024-01-03";
        DateTimeFormatter dateFormatter125 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate125 = LocalDate.parse(initialEndDate125, dateFormatter125);

        Posts posts125 = Posts.builder()
                .user(user124)
                .postType(PostType.PROJECT)
                .title("언리얼로 메이플스토리같은 RPG 만들자!")
                .recruitmentCount(4)
                .content("졸업프로젝트로 큰 규모로 RPG 게임 개발해봅시다.\n언리얼 장인 세 분 모십니다.\n초 고퀄 게임 Coming soon...")
                .endDate(endDate125)
                .build();

        postsRepository.save(posts125);

        Category category125 = Category.builder()
                .posts(posts125)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category125.validateFieldCount();
        categoryRepository.save(category125);




        // 웹, AI
        User user126 = User.builder()
                .userName("하냥이")
                .nickName("야옹이집사")
                .email("catmom@g.hongik.ac.kr")
                .password(passwordEncoder.encode("126"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile34.jpg")
                .build();

        userRepository.save(user126);

        Portfolio user126Portfolio = Portfolio.builder()
                .user(user126)
                .web(4)
                .app(0)
                .game(0)
                .ai(3)
                .shortIntroduce("고양이 집사")
                .introduce("- 웹 개발에 관심 있어용\n- 인공지능에도 관심있어용\n- 고양이에도 관심있어용")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user126Portfolio);

        String initialEndDate126 = "2024-02-14";
        DateTimeFormatter dateFormatter126 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate126 = LocalDate.parse(initialEndDate126, dateFormatter126);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts126 = Posts.builder()
                .user(user126)
                .postType(PostType.STUDY)
                .title("인공지능 스터디")
                .recruitmentCount(3)
                .content("인공지능 어디서부터 어떻게 시작해야 하죠?\n파이토치든 텐서플로든 함께 공부해요~\n")
                .endDate(endDate126)
                .build();

        postsRepository.save(posts126);

        Category category126 = Category.builder()
                .posts(posts126)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category126.validateFieldCount();
        categoryRepository.save(category126);

        String initialEndDate127 = "2024-01-03";
        DateTimeFormatter dateFormatter127 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate127 = LocalDate.parse(initialEndDate127, dateFormatter127);

        Posts posts127 = Posts.builder()
                .user(user126)
                .postType(PostType.PROJECT)
                .title("인공지능으로 추천기능 구현한 플젝")
                .recruitmentCount(4)
                .content("AWS 활용해서 배포까지 하는 웹 서비스 만들어봐요~\n인공지능 쓸거에요!\n스프링 부트와 장고 중에서 백엔드 고민 중..\nTypeScript 가능한 프론트 개발자도 모셔요!")
                .endDate(endDate127)
                .build();

        postsRepository.save(posts127);

        Category category127 = Category.builder()
                .posts(posts127)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category127.validateFieldCount();
        categoryRepository.save(category127);




        // 앱, AI
        User user128 = User.builder()
                .userName("한강쥐")
                .nickName("개팔자상팔자개발자")
                .email("dogmom@g.hongik.ac.kr")
                .password(passwordEncoder.encode("128"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile35.jpg")
                .build();

        userRepository.save(user128);

        Portfolio user128Portfolio = Portfolio.builder()
                .user(user128)
                .web(0)
                .app(4)
                .game(0)
                .ai(3)
                .shortIntroduce("개팔자는 상팔자. 개발자도 상팔자.")
                .introduce("- 앱 개발에 관심 있어용\n- 인공지능에도 관심있어용\n- 강아지에도 관심있어용")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user128Portfolio);

        String initialEndDate128 = "2024-02-14";
        DateTimeFormatter dateFormatter128 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate128 = LocalDate.parse(initialEndDate128, dateFormatter128);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts128 = Posts.builder()
                .user(user128)
                .postType(PostType.STUDY)
                .title("앱 개발 스터디")
                .recruitmentCount(3)
                .content("앱에 관심 있으신 분\n어느 정도 앱 개발을 해보신 분\n프론트엔드에 관심있는 분\n백엔드에 관심있는 분")
                .endDate(endDate128)
                .build();

        postsRepository.save(posts128);

        Category category128 = Category.builder()
                .posts(posts128)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category128.validateFieldCount();
        categoryRepository.save(category128);

        String initialEndDate129 = "2024-02-04";
        DateTimeFormatter dateFormatter129 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate129 = LocalDate.parse(initialEndDate129, dateFormatter129);

        Posts posts129 = Posts.builder()
                .user(user128)
                .postType(PostType.PROJECT)
                .title("인공지능으로 앱 플젝")
                .recruitmentCount(3)
                .content("깃허브 활용해서 배포까지 하는 앱 서비스 만들어봐요~\n졸업 프로젝트 용이에요!\n스프링 부트로 백서버 구축합시다.\nSwift 쓸 줄 아시는 분도 구합니당!")
                .endDate(endDate129)
                .build();

        postsRepository.save(posts129);

        Category category129 = Category.builder()
                .posts(posts129)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category129.validateFieldCount();
        categoryRepository.save(category129);










        // 초기 데이터 생성 및 저장 (시홍)
        User user200 = User.builder()
                .userName("김성준")
                .nickName("sungjoon")
                .email("sungjoon@g.hongik.ac.kr")
                .password(passwordEncoder.encode("200"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile36.jpg")
                .build();

        userRepository.save(user200);

        Portfolio user200Portfolio = Portfolio.builder()
                .user(user200)
                .web(4)
                .app(2)
                .game(1)
                .ai(3)
                .shortIntroduce("플젝 잘하고싶다....")
                .introduce("- 스프링 관련 웹 개발 경험 \n- NodeJs, React로 풀스택 개발 경험 \n- 현재 jwt 관련해 공부 중")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user200Portfolio);

        String initialEndDate200 = "2023-12-02";
        DateTimeFormatter dateFormatter200 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate200 = LocalDate.parse(initialEndDate200, dateFormatter200);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts130 = Posts.builder()
                .user(user200)
                .postType(PostType.STUDY)
                .title("c++로 코테 준비하실 분 저랑 같이 백준 공부해요")
                .recruitmentCount(4)
                .content("C++로 코테 제대로 하실 분만 모십니다.\n주 1~2회 오프라인으로도 만나고 싶습니다.\n각종 부트캠프도 같이 준비하면 좋을 것 같아요")
                .endDate(endDate200)
                .build();

        postsRepository.save(posts130);

        Category category130 = Category.builder()
                .posts(posts130)
                .web(false)
                .app(false)
                .game(true)
                .ai(true)
                .build();

        category130.validateFieldCount();
        categoryRepository.save(category130);

        String initialEndDate201 = "2023-12-10";
        DateTimeFormatter dateFormatter201 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate201 = LocalDate.parse(initialEndDate201, dateFormatter201);

        Posts posts131 = Posts.builder()
                .user(user200)
                .postType(PostType.PROJECT)
                .title("망고플레이트 대체할 홍대맛집 사이트 만들려고 합니다~")
                .recruitmentCount(4)
                .content("Spring으로 백엔드 구축 예정이고,\n프런트는 react 가능하신 분이 지원하셨으면 좋겠습니다")
                .endDate(endDate201)
                .build();

        postsRepository.save(posts131);

        Category category131 = Category.builder()
                .posts(posts131)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category131.validateFieldCount();
        categoryRepository.save(category131);



        // 알림 앱 개발
        User user201 = User.builder()
                .userName("이알람")
                .nickName("alimi")
                .email("alimi@g.hongik.ac.kr")
                .password(passwordEncoder.encode("201"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile37.jpg")
                .build();

        userRepository.save(user201);

        Portfolio user201Portfolio = Portfolio.builder()
                .user(user201)
                .web(3)
                .app(4)
                .game(0)
                .ai(0)
                .shortIntroduce("간단하지만, 있으면 좋을 앱들을 만드는 데에 관심이 많습니다.~")
                .introduce("- Kotlin 사용 경험 다수 \n- React Native 활용 경험 \n- 메신저 앱 개발 경험 보유")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user201Portfolio);

        String initialEndDate202 = "2024-01-12";
        DateTimeFormatter dateFormatter202 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate202 = LocalDate.parse(initialEndDate202, dateFormatter202);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts132 = Posts.builder()
                .user(user201)
                .postType(PostType.STUDY)
                .title("앱 개발에 관련된 인프런 강좌 공구해서 같이 공부할 분 모집합니다")
                .recruitmentCount(3)
                .content("Kotlin과 Android Studio를 활용한 강의가 좋을 것 같습니다.\n저도 아직 초보라 같이 공부하시면서 성장하실 분이면 좋아요\n제 이메일로 연락 주세요!")
                .endDate(endDate202)
                .build();

        postsRepository.save(posts132);

        Category category132 = Category.builder()
                .posts(posts132)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category132.validateFieldCount();
        categoryRepository.save(category132);

        String initialEndDate203 = "2024-01-17";
        DateTimeFormatter dateFormatter203 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate203 = LocalDate.parse(initialEndDate203, dateFormatter203);

        Posts posts133 = Posts.builder()
                .user(user201)
                .postType(PostType.PROJECT)
                .title("학원가에서 사용할 커스터마이징 알람 앱 같이 만들분 구해요")
                .recruitmentCount(3)
                .content("Swift 사용가능하신 분이면 진짜 꼭 연락주세요.... \n저도 현재 공부중인데, 아직 갈 길이 머네요! 한 2달 안에 만드는 걸 목표로 하고 있습니다")
                .endDate(endDate203)
                .build();

        postsRepository.save(posts133);

        Category category133 = Category.builder()
                .posts(posts133)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category133.validateFieldCount();
        categoryRepository.save(category133);




        // 웹, 앱, ai, 게임
        User user202 = User.builder()
                .userName("송하나")
                .nickName("oneSong")
                .email("oneSong@g.hongik.ac.kr")
                .password(passwordEncoder.encode("202"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile38.jpg")
                .build();

        userRepository.save(user202);

        Portfolio user202Portfolio = Portfolio.builder()
                .user(user202)
                .web(4)
                .app(3)
                .game(1)
                .ai(2)
                .shortIntroduce("프론트만 팠습니다. 믿어주세요.")
                .introduce("- 각종 프로젝트 프론트 다수 경험 \n- ux,ui관련 공부도 병행 줌 \n- 코테는 python으로 합니다.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user202Portfolio);

        String initialEndDate204 = "2024-02-22";
        DateTimeFormatter dateFormatter204 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate204 = LocalDate.parse(initialEndDate204, dateFormatter204);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts134 = Posts.builder()
                .user(user202)
                .postType(PostType.STUDY)
                .title("파이썬으로 프로그래머스 문풀 같이 하실분")
                .recruitmentCount(3)
                .content("프로그래머스 lv3,4 같이 도전하면서 푸실 분\n꾸준히 하시는 분만 연락주세요\n야생형 개발자 환영")
                .endDate(endDate204)
                .build();

        postsRepository.save(posts134);

        Category category134 = Category.builder()
                .posts(posts134)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category134.validateFieldCount();
        categoryRepository.save(category134);

        String initialEndDate205 = "2024-02-07";
        DateTimeFormatter dateFormatter205 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate205 = LocalDate.parse(initialEndDate205, dateFormatter205);

        Posts posts135 = Posts.builder()
                .user(user202)
                .postType(PostType.PROJECT)
                .title("홍대생을 위한 취업 정보 알리미 사이트 제작")
                .recruitmentCount(5)
                .content("노드js 잘 다루시는 백엔드 3명 구합니다.\nFigma, React, Typescript 잘 다루는 프론트엔드 2명 구합니다.")
                .endDate(endDate205)
                .build();

        postsRepository.save(posts135);

        Category category135 = Category.builder()
                .posts(posts135)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category135.validateFieldCount();
        categoryRepository.save(category135);




        // 앱, ai
        User user203 = User.builder()
                .userName("강철민")
                .nickName("fubao")
                .email("fubao@g.hongik.ac.kr")
                .password(passwordEncoder.encode("203"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile39.jpg")
                .build();

        userRepository.save(user203);

        Portfolio user203Portfolio = Portfolio.builder()
                .user(user203)
                .web(1)
                .app(4)
                .game(2)
                .ai(3)
                .shortIntroduce("동물과 앱 공부를 사랑하는 예비 개발자 fubao입니다")
                .introduce("- '오늘의 동물원' 앱 제작 \n- '동물 먹이도감' 앱 제작 \n- 풀스택 지망")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user203Portfolio);

        String initialEndDate206 = "2024-01-10";
        DateTimeFormatter dateFormatter206 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate206 = LocalDate.parse(initialEndDate206, dateFormatter206);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts136 = Posts.builder()
                .user(user203)
                .postType(PostType.STUDY)
                .title("figma 관련해서 가르쳐주실 분 모십니다.")
                .recruitmentCount(2)
                .content("피그마 잘 아시는 분 모십니다. 당장 프로젝트에 써야하는데, 잘 몰라서...\n이메일로 먼전 연락주세요\n")
                .endDate(endDate206)
                .build();

        postsRepository.save(posts136);

        Category category136 = Category.builder()
                .posts(posts136)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category136.validateFieldCount();
        categoryRepository.save(category136);

        String initialEndDate207 = "2024-01-06";
        DateTimeFormatter dateFormatter207 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate207 = LocalDate.parse(initialEndDate207, dateFormatter207);

        Posts posts137 = Posts.builder()
                .user(user203)
                .postType(PostType.PROJECT)
                .title("동물 심리 분석 측정 앱")
                .recruitmentCount(4)
                .content("동물들의 얼굴 사진을 보고, 표정을 기반으로 동물의 심리 상태 및 감정을 분석해주는 앱을 제장하려고 합니다.\n 풀스택 가능하신 분 3분 모집합니다.\n")
                .endDate(endDate207)
                .build();

        postsRepository.save(posts137);

        Category category137 = Category.builder()
                .posts(posts137)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category137.validateFieldCount();
        categoryRepository.save(category137);




        // 게임, 앱
        User user204 = User.builder()
                .userName("김뮤즈")
                .nickName("bandBoy")
                .email("bandBoy@g.hongik.ac.kr")
                .password(passwordEncoder.encode("204"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile40.jpg")
                .build();

        userRepository.save(user204);

        Portfolio user204Portfolio = Portfolio.builder()
                .user(user204)
                .web(0)
                .app(3)
                .game(4)
                .ai(0)
                .shortIntroduce("게임이랑 앱에 관심있는 예비 개발자입니다. 잘 부탁드려요!")
                .introduce("- 앱 관련 공부는 했습니다만, 구체적인 프로젝트 경험은 아직 없습니다.\n- 저랑 관심사 비슷하신 분 같이 공부하면서 성장해요!")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user204Portfolio);

        String initialEndDate208 = "2024-01-11";
        DateTimeFormatter dateFormatter208 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate208 = LocalDate.parse(initialEndDate208, dateFormatter208);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts138 = Posts.builder()
                .user(user204)
                .postType(PostType.STUDY)
                .title("유니티 스터디 모집")
                .recruitmentCount(4)
                .content("유니티 처음 같이 배우실 분 모집해요\n인프런 강의 보고 각자 공부하고, 서로 피드백 주는 방식으로 하면 좋을 것 같습니다!!\n")
                .endDate(endDate208)
                .build();

        postsRepository.save(posts138);

        Category category138 = Category.builder()
                .posts(posts138)
                .web(false)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category138.validateFieldCount();
        categoryRepository.save(category138);

        String initialEndDate209 = "2024-01-16";
        DateTimeFormatter dateFormatter209 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate209 = LocalDate.parse(initialEndDate209, dateFormatter209);

        Posts posts139 = Posts.builder()
                .user(user204)
                .postType(PostType.PROJECT)
                .title("리듬 게임 앱 만드실 분 구합니다")
                .recruitmentCount(3)
                .content("리듬스타에서 갑자기 영감 받아서 글 올립니다.\n게임 개발 경험 있으시고, 음악 관련 기본 지식 있으신 분이면 좋을 것 같아요.\n가능하면, 유니티 사용해보고자 합니다.")
                .endDate(endDate209)
                .build();

        postsRepository.save(posts139);

        Category category139 = Category.builder()
                .posts(posts139)
                .web(false)
                .app(true)
                .game(true)
                .ai(false)
                .build();

        category139.validateFieldCount();
        categoryRepository.save(category139);




        // 웹, 게임, ai, 앱
        User user205 = User.builder()
                .userName("정한별")
                .nickName("takoyam")
                .email("takoyam@g.hongik.ac.kr")
                .password(passwordEncoder.encode("205"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile41.jpg")
                .build();

        userRepository.save(user205);

        Portfolio user205Portfolio = Portfolio.builder()
                .user(user205)
                .web(4)
                .app(2)
                .game(3)
                .ai(1)
                .shortIntroduce("게임을 좋아하지만, 직무는 웹쪽 희망합니다")
                .introduce("- 코테 언어는 파이썬입니다. \n- 백엔드 공부는 김영한 커리 타면서 공부해보고 있어요. \n- 빠른 시일 내에 프로젝트 해보고 싶습니다.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user205Portfolio);

        String initialEndDate210 = "2024-01-03";
        DateTimeFormatter dateFormatter210 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate210 = LocalDate.parse(initialEndDate210, dateFormatter210);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts140 = Posts.builder()
                .user(user205)
                .postType(PostType.STUDY)
                .title("spring jpa 활용")
                .recruitmentCount(3)
                .content("김영한 커리 타시는 분들 중에, 각자 서로 과제 내주고 피드백 주는 스터디 하실 분 있나요?\n아직 실력이 부족해서, 그런 방식으로 공부하면 좋을 것 같습니다.\n")
                .endDate(endDate210)
                .build();

        postsRepository.save(posts140);

        Category category140 = Category.builder()
                .posts(posts140)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category140.validateFieldCount();
        categoryRepository.save(category140);

        String initialEndDate211 = "2024-01-11";
        DateTimeFormatter dateFormatter211 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate211 = LocalDate.parse(initialEndDate211, dateFormatter211);

        Posts posts141 = Posts.builder()
                .user(user205)
                .postType(PostType.PROJECT)
                .title("홍익대 중고 거래 장터 사이트 같이 제작하실 분")
                .recruitmentCount(3)
                .content("홍익대 내에서 사용될 중고 거래 장터 사이트 같이 제작하실 분 모집합니다.\n백엔드 1, 프론트 1명 구해요.\n아마 Spring / Vue 사용할 것 같습니다")
                .endDate(endDate211)
                .build();

        postsRepository.save(posts141);

        Category category141 = Category.builder()
                .posts(posts141)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category141.validateFieldCount();
        categoryRepository.save(category141);




        // 게임, 웹, ai, 앱(플래시게임)
        User user206 = User.builder()
                .userName("김빛나")
                .nickName("flashKim")
                .email("flashKim@g.hongik.ac.kr")
                .password(passwordEncoder.encode("206"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile42.jpg")
                .build();

        userRepository.save(user206);

        Portfolio user206Portfolio = Portfolio.builder()
                .user(user206)
                .web(3)
                .app(1)
                .game(4)
                .ai(2)
                .shortIntroduce("고전 플래쉬 게임을 좋아하는 인디 게임 전문가 김빛나입니다.")
                .introduce("- 코테 언어는 c# 사용합니다. \n- 유니티로 게임 제작 2번 해봤어요. \n- 제 닉네임으로 된 깃허브 가보시면 확인하실 수 있습니다.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user206Portfolio);

        String initialEndDate212 = "2024-01-01";
        DateTimeFormatter dateFormatter212 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate212 = LocalDate.parse(initialEndDate212, dateFormatter212);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts142 = Posts.builder()
                .user(user206)
                .postType(PostType.STUDY)
                .title("언리얼 스터디")
                .recruitmentCount(3)
                .content("유니티에서 이제 언리얼 넘어가서 공부해보려고 합니다.\n잘 하는 분 환영\n못하는 분도 환영")
                .endDate(endDate212)
                .build();

        postsRepository.save(posts142);

        Category category142 = Category.builder()
                .posts(posts142)
                .web(false)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category142.validateFieldCount();
        categoryRepository.save(category142);

        String initialEndDate213 = "2024-01-11";
        DateTimeFormatter dateFormatter213 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate213 = LocalDate.parse(initialEndDate213, dateFormatter213);

        Posts posts143 = Posts.builder()
                .user(user206)
                .postType(PostType.PROJECT)
                .title("고전 인디 플래쉬 게임 개발")
                .recruitmentCount(3)
                .content("고전 인디 게임 좋아하시는 분 있으시면 같이 게임 개발 해봐요.\n 웹 상에 배포할거라, 백엔드 개발자 1명, 유니티 다루실 개발자 1명 구해요.\n")
                .endDate(endDate213)
                .build();

        postsRepository.save(posts143);

        Category category143 = Category.builder()
                .posts(posts143)
                .web(true)
                .app(false)
                .game(true)
                .ai(false)
                .build();

        category143.validateFieldCount();
        categoryRepository.save(category143);




        // AI, 웹, 앱 게임
        User user207 = User.builder()
                .userName("안준희")
                .nickName("mandooking")
                .email("mandooking@g.hongik.ac.kr")
                .password(passwordEncoder.encode("207"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile43.jpg")
                .build();

        userRepository.save(user207);

        Portfolio user207Portfolio = Portfolio.builder()
                .user(user207)
                .web(3)
                .app(2)
                .game(1)
                .ai(4)
                .shortIntroduce("예비 딥러닝 개발자 만두킹입니다.")
                .introduce("- 파이썬과 텐서플로우로 ai 공부 해보고 있습니다.. \n- 파이토치도 사용해본 적 있어요 \n- 웹 개발도 관심 있습니다.")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user207Portfolio);

        String initialEndDate214 = "2024-01-06";
        DateTimeFormatter dateFormatter214 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate214 = LocalDate.parse(initialEndDate214, dateFormatter214);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts144 = Posts.builder()
                .user(user207)
                .postType(PostType.STUDY)
                .title("파이토치 공부 같이 해여")
                .recruitmentCount(2)
                .content("파이토치 스터디 같이 해보고, 인연 맞으면 졸프까지.... 함께합시다 우리\n")
                .endDate(endDate214)
                .build();

        postsRepository.save(posts144);

        Category category144 = Category.builder()
                .posts(posts144)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category144.validateFieldCount();
        categoryRepository.save(category144);

        String initialEndDate215 = "2024-01-21";
        DateTimeFormatter dateFormatter215 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate215 = LocalDate.parse(initialEndDate215, dateFormatter215);

        Posts posts145 = Posts.builder()
                .user(user207)
                .postType(PostType.PROJECT)
                .title("음성변조를 활용한 ai 면접 프로그램 같이 만들어보실 분 구해요")
                .recruitmentCount(5)
                .content("인공지능을 활용한 ai 면접 프로그램 구상 완료됐어요.\n이미 기획, ui/ux 모두 모였고, 백 2분, 프론트 3분 더 모집합니다..\n")
                .endDate(endDate215)
                .build();

        postsRepository.save(posts145);

        Category category145 = Category.builder()
                .posts(posts145)
                .web(true)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category145.validateFieldCount();
        categoryRepository.save(category145);




        // ai, 앱 게임, 웹
        User user208 = User.builder()
                .userName("김중사")
                .nickName("McTominay")
                .email("mctominay@g.hongik.ac.kr")
                .password(passwordEncoder.encode("208"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile44.jpg")
                .build();

        userRepository.save(user208);

        Portfolio user208Portfolio = Portfolio.builder()
                .user(user208)
                .web(1)
                .app(3)
                .game(1)
                .ai(4)
                .shortIntroduce("축구 관련 앱 제작에 관심 많은 김중사 입니다.")
                .introduce("- 축구에 관심 많아, 스포츠 관련 앱 개발에 관심 있습니다. \n- 축구 자세 교정 앱 개발(진행 중) \n")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user208Portfolio);

        String initialEndDate216 = "2024-02-27";
        DateTimeFormatter dateFormatter216 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate216 = LocalDate.parse(initialEndDate216, dateFormatter216);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts146 = Posts.builder()
                .user(user208)
                .postType(PostType.STUDY)
                .title("딥러닝 강의 사서 같이 공부하실 분 모집")
                .recruitmentCount(3)
                .content("인공지능 공부를 해야할 필요성을 느껴, 별도의 인강을 사서 같이 스터디하실 분 모집합니다.\n만약 잘하시는 분 있으시면, 절 가르치셔도 좋아요...")
                .endDate(endDate216)
                .build();

        postsRepository.save(posts146);

        Category category146 = Category.builder()
                .posts(posts146)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category146.validateFieldCount();
        categoryRepository.save(category146);

        String initialEndDate217 = "2024-01-19";
        DateTimeFormatter dateFormatter217 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate217 = LocalDate.parse(initialEndDate217, dateFormatter217);

        Posts posts147 = Posts.builder()
                .user(user208)
                .postType(PostType.PROJECT)
                .title("축구 슈팅 자세 교정 앱 개발자 모집")
                .recruitmentCount(3)
                .content("텐서플로우를 활용한 축구 슈팅 자세 보정 앱을 제작 중에 있습니다.\n만약 관심 있는 분이 있으시면, 같이 프로젝트 합류해주세요! 코틀린, 텐서플로우 가능하신 분 모십니다.\n")
                .endDate(endDate217)
                .build();

        postsRepository.save(posts147);

        Category category147 = Category.builder()
                .posts(posts147)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category147.validateFieldCount();
        categoryRepository.save(category147);



        // ai, 앱, 웹, 게임
        User user209 = User.builder()
                .userName("김우석")
                .nickName("snuStudent")
                .email("snusnu@g.hongik.ac.kr")
                .password(passwordEncoder.encode("209"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile45.jpg")
                .build();

        userRepository.save(user209);

        Portfolio user209Portfolio = Portfolio.builder()
                .user(user209)
                .web(2)
                .app(3)
                .game(1)
                .ai(4)
                .shortIntroduce("의료 관련 딥러닝에 관심 있는 개발자입니다.")
                .introduce("- 의료 산업 관련 db 구축 경험 보유\n- 대학병원 전산실에서 인턴 근무 경험 보유 \n- 비디오 이미지 프로세싱에도 관심 있음\n")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user209Portfolio);

        String initialEndDate218 = "2024-01-22";
        DateTimeFormatter dateFormatter218 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate218 = LocalDate.parse(initialEndDate218, dateFormatter218);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts148 = Posts.builder()
                .user(user209)
                .postType(PostType.STUDY)
                .title("비디오 이미지 프로세싱 관련 스터디")
                .recruitmentCount(4)
                .content("교수님께서 진행하시는 이미지 프로세싱 수업 듣고 서로 공부 한 거 피드백 주는 스터디 하고싶어요.\n총 4명으로 구성 예정이고, 모집 마감되는대로 바로 시작할 예정입니다!")
                .endDate(endDate218)
                .build();

        postsRepository.save(posts148);

        Category category148 = Category.builder()
                .posts(posts148)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category148.validateFieldCount();
        categoryRepository.save(category148);

        String initialEndDate219 = "2024-01-06";
        DateTimeFormatter dateFormatter219 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate219 = LocalDate.parse(initialEndDate219, dateFormatter219);

        Posts posts149 = Posts.builder()
                .user(user209)
                .postType(PostType.PROJECT)
                .title("치과 교육 자료로 사용될 비디오 이미지 프로세싱 관련 프로젝트 하실 분 모집합니다. ")
                .recruitmentCount(4)
                .content("학습을 위한 가상 수술 자료를 비디오 이미지 프로세싱 통해 앱으로 제작해 배포하고자합니다. \n관련 수업 들으신 분 환영합니다. 앱쪽 지식 있으신 분들도 지원해주세요!!\n")
                .endDate(endDate219)
                .build();

        postsRepository.save(posts149);

        Category category149 = Category.builder()
                .posts(posts149)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category149.validateFieldCount();
        categoryRepository.save(category149);



////////////////////////여기부터 내일
        // 앱 웹 ai 게임
        User user210 = User.builder()
                .userName("최유리")
                .nickName("glassChoi")
                .email("glasschoi@g.hongik.ac.kr")
                .password(passwordEncoder.encode("210"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile46.jpg")
                .build();

        userRepository.save(user210);

        Portfolio user210Portfolio = Portfolio.builder()
                .user(user210)
                .web(3)
                .app(4)
                .game(1)
                .ai(2)
                .shortIntroduce("모바일 앱 개발자 준비생입니다.")
                .introduce("- 안녕하세요!\n- 모바일 앱에 관심있는 컴퓨터공학과 18학번입니다.\n- swift를 전문적으로 배우고 있어요")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user210Portfolio);

        String initialEndDate220 = "2024-01-09";
        DateTimeFormatter dateFormatter220 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate220 = LocalDate.parse(initialEndDate220, dateFormatter220);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts150 = Posts.builder()
                .user(user210)
                .postType(PostType.STUDY)
                .title("swift 같이 공부하실 분 모집합니다!")
                .recruitmentCount(4)
                .content("예비 애플 개발자분들! 우리 swift공부 열심히 해서 해외로 갑시다.\n장기적으로 스터디 가능하신분이면 제게 메일로 연락주세요.." +
                        "\n구체적인 커리큘럼, 일정에 대해 궁금하시면 댓글 달아주세요!")
                .endDate(endDate220)
                .build();

        postsRepository.save(posts150);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image150 = PostsFiles.builder()
                .posts(posts150) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYJ2.png") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image150);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file150 = PostsFiles.builder()
                .posts(posts150) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file10.pdf") // 첨부 파일의 UUID
                .fileName("file10.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file150);

        Category category150 = Category.builder()
                .posts(posts150)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category150.validateFieldCount();
        categoryRepository.save(category150);

        String initialEndDate221 = "2024-01-06";
        DateTimeFormatter dateFormatter221 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate221 = LocalDate.parse(initialEndDate221, dateFormatter221);

        Posts posts151 = Posts.builder()
                .user(user210)
                .postType(PostType.PROJECT)
                .title("피트니스 강사들을 위한 애플 워치 앱 개발하실 분!")
                .recruitmentCount(4)
                .content("제가 아는 강사분들한테 여쭤봤는데, 피트니스 수업 하면서, 운동 루틴 프로세스 알려주는 워치 앱 있으면 좋을 것 같다는 의견이 많았다고 하시더라구요" +
                        "이런 앱 있으면 아마 많이 사용할 것 같아요! 이걸로 졸프 같이할 사람 구합니다.\nswift개발자 2명, ui/ux 1분 모집합니다.\n")
                .endDate(endDate221)
                .build();

        postsRepository.save(posts151);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image151 = PostsFiles.builder()
                .posts(posts151) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYJ1.png") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image151);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file151 = PostsFiles.builder()
                .posts(posts151) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file9.pdf") // 첨부 파일의 UUID
                .fileName("file9.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file151);

        Category category151 = Category.builder()
                .posts(posts151)
                .web(false)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category151.validateFieldCount();
        categoryRepository.save(category151);




        // 웹 앱 게임 ai
        User user211 = User.builder()
                .userName("김로코")
                .nickName("loptimist")
                .email("loptimist@g.hongik.ac.kr")
                .password(passwordEncoder.encode("211"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile47.jpg")
                .build();

        userRepository.save(user211);

        Portfolio user211Portfolio = Portfolio.builder()
                .user(user211)
                .web(4)
                .app(3)
                .game(2)
                .ai(1)
                .shortIntroduce("홍익대에서 열심히 해서 우아한 형제들 가겠습니다.")
                .introduce("- 백엔드 구축 위주로 공부하고 있습니다.\n- spring 관련 경험 개발 다수\n- spring security/jwt을 활용한 개발 선호")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user211Portfolio);

        String initialEndDate222 = "2024-02-19";
        DateTimeFormatter dateFormatter222 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate222 = LocalDate.parse(initialEndDate222, dateFormatter222);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts152 = Posts.builder()
                .user(user211)
                .postType(PostType.STUDY)
                .title("네트워크 공부")
                .recruitmentCount(3)
                .content("컴퓨터 네트워크, 웹 소켓 관련 스터디 하실 분 모집합니다.\n가능하면 웹 개발 하시는 분들이면 좋겠습니다!\n메일 주세요")
                .endDate(endDate222)
                .build();

        postsRepository.save(posts152);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image152 = PostsFiles.builder()
                .posts(posts152) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYS4.jpg") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image152);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file152 = PostsFiles.builder()
                .posts(posts152) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file8.pdf") // 첨부 파일의 UUID
                .fileName("file8.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file152);

        Category category152 = Category.builder()
                .posts(posts152)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category152.validateFieldCount();
        categoryRepository.save(category152);

        String initialEndDate223 = "2024-02-03";
        DateTimeFormatter dateFormatter223 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate223 = LocalDate.parse(initialEndDate223, dateFormatter223);

        Posts posts153 = Posts.builder()
                .user(user211)
                .postType(PostType.PROJECT)
                .title("학교 커뮤니티 웹 사이트 개발하실 분 모집합니다")
                .recruitmentCount(3)
                .content("홍대생을 대상으로 하는 커뮤니티 사이트 만들고자 합니다.\n구체적인 내용은 추후에 만나서 상의해요." +
                        "스프링 부트 가능 백엔드 1명, react가능 프론트 1명 구합니다.\n")
                .endDate(endDate223)
                .build();

        postsRepository.save(posts153);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image153 = PostsFiles.builder()
                .posts(posts153) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYS3.jpg") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image153);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file153 = PostsFiles.builder()
                .posts(posts153) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file7.pdf") // 첨부 파일의 UUID
                .fileName("file7.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file153);

        Category category153 = Category.builder()
                .posts(posts153)
                .web(true)
                .app(false)
                .game(false)
                .ai(false)
                .build();

        category153.validateFieldCount();
        categoryRepository.save(category153);




        // 게임, ai
        User user212 = User.builder()
                .userName("김펍지")
                .nickName("battlemaster")
                .email("battlemaster@g.hongik.ac.kr")
                .password(passwordEncoder.encode("212"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile48.jpg")
                .build();

        userRepository.save(user212);

        Portfolio user212Portfolio = Portfolio.builder()
                .user(user212)
                .web(0)
                .app(0)
                .game(4)
                .ai(3)
                .shortIntroduce("gta같은 게임을 좋아하는 혼모노 게임 개발자입니다")
                .introduce("- 언리얼 관련 공부를 하고 있습니다.\n- ai활용한 게임 만들고 싶습니다.\n- rpg게임 '제노아' 개발해본 적 있습니다")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user212Portfolio);

        String initialEndDate224 = "2024-02-29";
        DateTimeFormatter dateFormatter224 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate224 = LocalDate.parse(initialEndDate224, dateFormatter224);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts154 = Posts.builder()
                .user(user212)
                .postType(PostType.STUDY)
                .title("ai 스터디")
                .recruitmentCount(3)
                .content("3학년 떄 ai관련 수업을 수강하지 못해서 배울 곳이 마땅히 없네요\n" +
                        "같이 관련 논문 보면서 공부하실 분 모집해요!")
                .endDate(endDate224)
                .build();

        postsRepository.save(posts154);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image154 = PostsFiles.builder()
                .posts(posts154) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYS2.jpg") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image154);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file154 = PostsFiles.builder()
                .posts(posts154) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file6.pdf") // 첨부 파일의 UUID
                .fileName("file6.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file154);

        Category category154 = Category.builder()
                .posts(posts154)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category154.validateFieldCount();
        categoryRepository.save(category154);

        String initialEndDate225 = "2024-01-03";
        DateTimeFormatter dateFormatter225 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate225 = LocalDate.parse(initialEndDate225, dateFormatter225);

        Posts posts155 = Posts.builder()
                .user(user212)
                .postType(PostType.PROJECT)
                .title("gta같은 ai모듈로 설계된 npc들 깔아놓은 규모 큰 게임 개발해보실 분 모집합니다")
                .recruitmentCount(5)
                .content("졸업프로젝트를 넘어서, 이걸로 스타트업까지 창업 생각있으신 분 저랑 함께 미래를 도모해봐요.\n세계관은 거의 구상 완료했습니다.\n" +
                        "ai관련 모듈 다뤄보신 분, 언리얼 가능하신 분만 지원해주세요.")
                .endDate(endDate225)
                .build();

        postsRepository.save(posts155);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image155 = PostsFiles.builder()
                .posts(posts155) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageYS1.jpg") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image155);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file155 = PostsFiles.builder()
                .posts(posts155) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file5.pdf") // 첨부 파일의 UUID
                .fileName("file5.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file155);

        Category category155 = Category.builder()
                .posts(posts155)
                .web(false)
                .app(false)
                .game(true)
                .ai(true)
                .build();

        category155.validateFieldCount();
        categoryRepository.save(category155);




        // 앱, AI
        User user213 = User.builder()
                .userName("안토샤")
                .nickName("듀얼페르소나")
                .email("persona@g.hongik.ac.kr")
                .password(passwordEncoder.encode("213"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile49.jpg")
                .build();

        userRepository.save(user213);

        Portfolio user213Portfolio = Portfolio.builder()
                .user(user213)
                .web(0)
                .app(4)
                .game(0)
                .ai(3)
                .shortIntroduce("커피를 좋아하는 컴공과 4학년 안토샤입니다.")
                .introduce("- 앱 개발에 관심 있습니다.\n- 학원에서 학생들 관리하는 앱 '우공방' 개발 \n- 독서실 좌석 배정 시스템 '내자리' 개발")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user213Portfolio);

        String initialEndDate226 = "2024-02-14";
        DateTimeFormatter dateFormatter226 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate226 = LocalDate.parse(initialEndDate226, dateFormatter226);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts156 = Posts.builder()
                .user(user213)
                .postType(PostType.STUDY)
                .title("파이어베이스 스터디하실 분")
                .recruitmentCount(3)
                .content("파이어베이스 같이 공부하실 분 모집합니다. \n기초강의 부터 커리 쭉 타서 공부하고, 이후에 마음 맞으면 플젝도 같이 해요!~\n")
                .endDate(endDate226)
                .build();

        postsRepository.save(posts156);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image156 = PostsFiles.builder()
                .posts(posts156) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageSH4.PNG") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image156);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file156 = PostsFiles.builder()
                .posts(posts156) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file4.pdf") // 첨부 파일의 UUID
                .fileName("file4.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file156);

        Category category156 = Category.builder()
                .posts(posts156)
                .web(true)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category156.validateFieldCount();
        categoryRepository.save(category156);

        String initialEndDate227 = "2024-01-03";
        DateTimeFormatter dateFormatter227 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate227 = LocalDate.parse(initialEndDate227, dateFormatter227);

        Posts posts157 = Posts.builder()
                .user(user213)
                .postType(PostType.PROJECT)
                .title("파이어베이스, aws 활용해서 토이 ERP 앱 만들어보실 분 모집해요!")
                .recruitmentCount(4)
                .content("AWS 활용해서 배포까지 하는 만들어봐요~\n" +
                        "소켓 공부해보신 분이면 바로 지원해주세요 ㅠㅠ\n" +
                        "앱 같이 공부하면서 플젝하실 분도 지원부탁합니닿ㅎ\n")
                .endDate(endDate227)
                .build();

        postsRepository.save(posts157);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image157 = PostsFiles.builder()
                .posts(posts157) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageSH3.PNG") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image157);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file157 = PostsFiles.builder()
                .posts(posts157) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file3.pdf") // 첨부 파일의 UUID
                .fileName("file3.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file157);

        Category category157 = Category.builder()
                .posts(posts157)
                .web(false)
                .app(true)
                .game(false)
                .ai(true)
                .build();

        category157.validateFieldCount();
        categoryRepository.save(category157);




        // 웹, 앱
        User user214 = User.builder()
                .userName("김현대")
                .nickName("현기차상반기합격")
                .email("hapgyuk@g.hongik.ac.kr")
                .password(passwordEncoder.encode("214"))  // 비밀번호 해싱
                .role(Role.USER)
                .lastAccessDate(LocalDateTime.of(2023, 7, 21, 14, 30, 0))
                .imageUrl("profile50.jpg")
                .build();

        userRepository.save(user214);

        Portfolio user214Portfolio = Portfolio.builder()
                .user(user214)
                .web(4)
                .app(3)
                .game(0)
                .ai(0)
                .shortIntroduce("대기업 si, 은행권 준비중인 4학년 김현대입니다.")
                .introduce("- 웹,앱이 주력이지만, 블록체인, 메타버스에도 관심 있습니다.\n- 주식 매매 사이트 개발 경험 보유\n- 경제학 부전공")
                //.fileUrl("")
                .build();

        portfolioRepository.save(user214Portfolio);

        String initialEndDate228 = "2024-02-14";
        DateTimeFormatter dateFormatter228 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate228 = LocalDate.parse(initialEndDate228, dateFormatter228);

        // Posts 생성자 : (User user, PostType postType, String title, Integer recruitmentCount, String content, String promoteImageUrl, String fileUrl, LocalDate endDate)
        Posts posts158 = Posts.builder()
                .user(user214)
                .postType(PostType.STUDY)
                .title("블록체인 스터디 모집")
                .recruitmentCount(3)
                .content("가상화폐, 블록체인 등등의 기술에 관심있으신 분 같이 공부하고 돈도 벌어봐요. \n" +
                        "관련 논문과, 교수님 수업 복습하며 공부 피드백하면서 학습할 예정입니다.\n" +
                        "관심 있으시면 지원해주세요!")
                .endDate(endDate228)
                .build();

        postsRepository.save(posts158);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image158 = PostsFiles.builder()
                .posts(posts158) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageSH2.PNG") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image158);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file158 = PostsFiles.builder()
                .posts(posts158) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file2.pdf") // 첨부 파일의 UUID
                .fileName("file2.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file158);

        Category category158 = Category.builder()
                .posts(posts158)
                .web(false)
                .app(false)
                .game(false)
                .ai(true)
                .build();

        category158.validateFieldCount();
        categoryRepository.save(category158);

        String initialEndDate229 = "2024-02-04";
        DateTimeFormatter dateFormatter229 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate229 = LocalDate.parse(initialEndDate229, dateFormatter229);

        Posts posts159 = Posts.builder()
                .user(user214)
                .postType(PostType.PROJECT)
                .title("내 주위 가장 저렴하고 좋은 독서실 찾기 프로젝트 같이 하실 분 모집합니다.")
                .recruitmentCount(3)
                .content("모바일 앱, 웹 두가지 방식으로 배포까지 할 분 모집합니다~\n기술 스택은 인원 모이면 정해도 괜찮아요!\n" +
                        "편하게 지원하시고, 궁금하신 점 있으면 댓글이나 메일 주세요!\n")
                .endDate(endDate229)
                .build();

        postsRepository.save(posts159);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles image159 = PostsFiles.builder()
                .posts(posts159) // 해당 이미지가 어떤 게시물에 속하는지 설정! (중요)
                .isImage(true) // 이미지 여부
                .fileUrl("imageSH1.PNG") // 이미지 파일의 UUID
                .build();

        // 이미지 posts_files 테이블에 저장
        postsFilesRepository.save(image159);

        // PostsFiles 엔티티 생성 및 저장할 리스트에 추가
        PostsFiles file159 = PostsFiles.builder()
                .posts(posts159) // 해당 첨부파일이 어떤 게시물에 속하는지 설정! (중요)
                .isImage(false) // 이미지 여부
                .fileUrl("file1.pdf") // 첨부 파일의 UUID
                .fileName("file1.pdf") // 첨부파일의 원본 이름
                .build();

        // 첨부파일 리스트를 한번에 posts_files 테이블에 저장
        postsFilesRepository.save(file159);

        Category category159 = Category.builder()
                .posts(posts159)
                .web(true)
                .app(true)
                .game(false)
                .ai(false)
                .build();

        category159.validateFieldCount();
        categoryRepository.save(category159);
    }


}
