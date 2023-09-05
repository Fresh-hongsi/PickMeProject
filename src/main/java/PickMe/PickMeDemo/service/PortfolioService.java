package PickMe.PickMeDemo.service;

import PickMe.PickMeDemo.dto.PortfolioDto;
import PickMe.PickMeDemo.dto.PortfolioFormDto;
import PickMe.PickMeDemo.entity.Portfolio;
import PickMe.PickMeDemo.entity.User;
import PickMe.PickMeDemo.exception.AppException;
import PickMe.PickMeDemo.repository.PortfolioRepository;
import PickMe.PickMeDemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository; // Add this if not already defined
    // Portfolio Mapper를 사용하고자 했으나, 이상하게 스프링 빈으로 등록이 안되어서, private final 변수로 사용할 수 없었음.
    
    
    // 포트폴리오 등록
    public PortfolioDto uploadPortfolio(PortfolioFormDto portfolioFormDto, String userEmail) {

        // Optional이므로, 해당 유저가 발견되면 유저를 반환, 해당 유저가 없으면 null 반환
        Optional<User> findUser = userRepository.findByEmail(userEmail);

        // orElseThrow(...): 이 메서드는 Optional 객체에서 호출됩니다.
        // 비어있는 경우 예외 객체를 생성하고 던질 람다 표현식을 받습니다.
        // 이 경우 Optional이 비어있는 경우(사용자를 찾지 못한 경우) "사용자를 찾을 수 없습니다"라는 메시지와 BAD_REQUEST (400) HTTP 상태 코드를 가진 AppException이 생성됩니다.
        User user = findUser.orElseThrow(() -> new AppException("사용자를 찾을 수 없습니다", HttpStatus.BAD_REQUEST));

        // Setter대신 생성자를 사용하여 Portfolio 테이블을 채움
        Portfolio registerPortfolio = Portfolio.builder()
                .user(user)
                .web(portfolioFormDto.getWeb())
                .app(portfolioFormDto.getApp())
                .game(portfolioFormDto.getGame())
                .ai(portfolioFormDto.getAi())
                .shortIntroduce(portfolioFormDto.getShortIntroduce())
                .introduce(portfolioFormDto.getIntroduce().replace("<br>", "\n"))
                .fileUrl(portfolioFormDto.getFileUrl())
                .build();

        // 포트폴리오 디비에 저장
        Portfolio portfolio = portfolioRepository.save(registerPortfolio);

        // 디비에 저장과는 별개로, 화면에 다시 데이터를 뿌려줄 PortfolioDto를 생성해서 반환
        // portfolioDto의 필드 : isCreated, nickName, email, web, app, game, ai, shortIntroduce, introduce, fileUrl
        // 포트폴리오를 생성하는 것이므로, isCreated를 true로 바로 저장
        PortfolioDto portfolioDto = PortfolioDto.builder()
                .isCreated(true)
                .nickName(user.getNickName())
                .email(user.getEmail())
                .web(portfolio.getWeb())
                .app(portfolio.getApp())
                .game(portfolio.getGame())
                .ai(portfolio.getAi())
                .shortIntroduce(portfolio.getShortIntroduce())
                .introduce(portfolio.getIntroduce())
                .fileUrl(portfolio.getFileUrl())
                .build();

        return portfolioDto;
    }



    // 나의 포트폴리오 조회
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = "user")
    public PortfolioDto getPortfolio(String userEmail) {
        // UserEmail을 통해 해당 User 찾기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        // User를 통해 User가 갖고 있는 포트폴리오 찾기
        Optional<Portfolio> findPortfolio = portfolioRepository.findByUser(user);

        // PortfolioDto를 빌더를 통해 생성
        PortfolioDto portfolioDto;

        if (findPortfolio.isPresent()) {
            // PortfolioDto를 빌더를 통해 생성
            portfolioDto = PortfolioDto.builder()
                    .isCreated(true)
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .web(findPortfolio.get().getWeb())
                    .app(findPortfolio.get().getApp())
                    .game(findPortfolio.get().getGame())
                    .ai(findPortfolio.get().getAi())
                    .shortIntroduce(findPortfolio.get().getShortIntroduce())
                    .introduce(findPortfolio.get().getIntroduce())
                    .fileUrl(findPortfolio.get().getFileUrl())
                    .build();
        }
        else {
            portfolioDto = PortfolioDto.builder()
                    .isCreated(false)
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .web(null)
                    .app(null)
                    .game(null)
                    .ai(null)
                    .shortIntroduce(null)
                    .introduce(null)
                    .fileUrl(null)
                    .build();
        }

        return portfolioDto;
    }


    // 포트폴리오 폼 조회
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = "user")
    public PortfolioFormDto getPortfolioForm(String userEmail) {
        // UserEmail을 통해 해당 User 찾기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        // User를 통해 User가 갖고 있는 포트폴리오 찾기
        Optional<Portfolio> portfolio = portfolioRepository.findByUser(user);

        PortfolioFormDto portfolioFormDto;

        if (portfolio.isPresent()) {
            portfolioFormDto = PortfolioFormDto.builder()
                    .hasPortfolio(true)
                    .web(user.getPortfolio().getWeb())
                    .app(user.getPortfolio().getApp())
                    .game(user.getPortfolio().getGame())
                    .ai(user.getPortfolio().getAi())
                    .shortIntroduce(user.getPortfolio().getShortIntroduce())
                    .introduce(user.getPortfolio().getIntroduce())
                    .fileUrl(user.getPortfolio().getFileUrl())
                    .build();
        }
        else {
            portfolioFormDto = PortfolioFormDto.builder()
                    .hasPortfolio(false)
                    .web(user.getPortfolio().getWeb())
                    .app(user.getPortfolio().getApp())
                    .game(user.getPortfolio().getGame())
                    .ai(user.getPortfolio().getAi())
                    .shortIntroduce(user.getPortfolio().getShortIntroduce())
                    .introduce(user.getPortfolio().getIntroduce())
                    .fileUrl(user.getPortfolio().getFileUrl())
                    .build();
        }
        // PortfolioDto를 빌더를 통해 생성


        return portfolioFormDto;
    }


    // Optional을 사용하여, portfolio를 찾았으면 true를 리턴, 찾지 못했으면 false를 리턴
    @Transactional(readOnly = true)
    public boolean hasPortfolio(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Optional<Portfolio> portfolioOptional = portfolioRepository.findByUser(user);

        // 해당 유저의 포트폴리오가 존재하면 true, 존재하지 않으면 false
        return portfolioOptional.isPresent();
    }


    // 포트폴리오 업데이트
    @EntityGraph(attributePaths = "user")
    public void updatePortfolio(String userEmail, PortfolioFormDto portfolioFormDto) {

        // UserEmail을 통해 해당 User 찾기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        // User를 통해 User가 갖고 있는 포트폴리오 찾기
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new AppException("포트폴리오를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        portfolio.setWeb(portfolioFormDto.getWeb());
        portfolio.setApp(portfolioFormDto.getApp());
        portfolio.setGame(portfolioFormDto.getGame());
        portfolio.setAi(portfolioFormDto.getAi());
        portfolio.setShortIntroduce(portfolioFormDto.getShortIntroduce());
        portfolio.setIntroduce(portfolioFormDto.getIntroduce());
        portfolio.setFileUrl(portfolioFormDto.getFileUrl());

        portfolioRepository.save(portfolio);
    }


    // 포트폴리오 삭제
    @EntityGraph(attributePaths = "user")
    public void deletePortfolio(String userEmail) {
        // userEmail로 user 찾기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("유저를 찾을 수 없습니다.",HttpStatus.NOT_FOUND));

        // User를 통해 User가 갖고 있는 포트폴리오 찾기
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new AppException("포트폴리오를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        portfolioRepository.delete(portfolio);
    }

    @Transactional(readOnly = true)
    public PortfolioDto getUserPortfolio(String nickName) {

        Optional<User> findUser = userRepository.findByNickName(nickName);

        // PortfolioDto를 빌더를 통해 생성
        PortfolioDto portfolioDto = PortfolioDto.builder()
                .isCreated(null)
                .nickName(null)
                .email(null)
                .web(null)
                .app(null)
                .game(null)
                .ai(null)
                .shortIntroduce(null)
                .introduce(null)
                .fileUrl(null)
                .build();

        // 유저가 있는 경우 정상적인 DTO 생성
        if (findUser.isPresent()) {
            // User를 통해 User가 갖고 있는 포트폴리오 찾기
            Optional<Portfolio> findUserPortfolio = portfolioRepository.findByUser(findUser.get());

            // 유저와 포트폴리오가 모두 있는 경우
            if (findUserPortfolio.isPresent()) {
                // PortfolioDto를 빌더를 통해 생성
                portfolioDto = PortfolioDto.builder()
                        .isCreated(true)
                        .nickName(findUser.get().getNickName())
                        .email(findUser.get().getEmail())
                        .web(findUserPortfolio.get().getWeb())
                        .app(findUserPortfolio.get().getApp())
                        .game(findUserPortfolio.get().getGame())
                        .ai(findUserPortfolio.get().getAi())
                        .shortIntroduce(findUserPortfolio.get().getShortIntroduce())
                        .introduce(findUserPortfolio.get().getIntroduce())
                        .fileUrl(findUserPortfolio.get().getFileUrl())
                        .build();
            }
            // 유저는 있는데 포트폴리오가 없는 경우
            else {
                portfolioDto = PortfolioDto.builder()
                        .isCreated(false)
                        .nickName(findUser.get().getNickName())
                        .email(findUser.get().getEmail())
                        .web(null)
                        .app(null)
                        .game(null)
                        .ai(null)
                        .shortIntroduce(null)
                        .introduce(null)
                        .fileUrl(null)
                        .build();
            }
        }

        return portfolioDto;
    }
}
