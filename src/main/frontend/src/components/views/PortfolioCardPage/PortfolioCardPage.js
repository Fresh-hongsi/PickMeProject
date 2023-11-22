// 로그인된 회원만 볼 수 있는 페이지
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Spin, Card, Row, Col, Divider, Button, Pagination, Menu, Dropdown } from 'antd';
import { CircularProgressbarWithChildren, buildStyles  } from 'react-circular-progressbar';

import { request, setSaveRecommendedList, setIsRecommededPortfolioView } from '../../../hoc/request';
import { saveRecommendedList, setRecommendPortfolioView } from '../../../_actions/actions';
//import { lastVisitedEndpoint } from '../../../_actions/actions';
//import { setLastVisitedEndpoint, setLastLastVisitedEndpoint, setLastLastLastVisitedEndpoint } from '../../../hoc/request';
import SearchInPortfolioCardPage from './SearchInPortfolioCardPage';

function PortfolioCardPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const selectedRecommendedList = useSelector(state => state.recommend.recommendedList);
    const selectedIsRecommededPortfolioView = useSelector(state => state.recommend.isRecommededPortfolioView);

    const [searchTerm, setSearchTerm] = useState(""); // 검색어 값 -  엔터나 클릭 시에만 변경
    const [currentSearchTerm, setCurrentSearchTerm] = useState(""); // 추적하는 검색어 값, 타이핑 시마다 변경
    const [relatedSearchTermEnable, setRelatedSearchTermEnable] = useState(true); // 연관 검색어 렌더링 필드 활성화 여부

    // 실시간 검색어 기반으로 가져온 연관 검색어 목록
    const [searchData, setSearchData] = useState({
        userSearchDtoList: [], //유저 이름 관련 최대 5개 가져옴
    });
    const [data, setData] = useState([]); // 백엔드에서 가져온 관련 포트폴리오 자료 값
    const [currentPage, setCurrentPage] = useState(0); // Java 및 Spring Boot를 포함한 페이징은 일반적으로 0부터 시작하므로 처음 이 페이지가 세팅될 떄는 0페이지(사실상 1페이지)로 삼음
    const [totalPages, setTotalPages] = useState(0); // 동적 쿼리를 날렸을 때 백엔드에서 주는 현재 상태에서의 total 페이지 수 세팅을 위함
    const [selectedBanners, setSelectedBanners] = useState(['all']); // 처음 해당 페이지가 setting될 떄는 선택된 배너가 '전체'가 되도록 함
    const [sortOption, setSortOption] = useState('latestPortfolio'); // 최신 등록 순 기본으로 선택
    const [reload, setReload] = useState(0);
    const [recommend, setRecommend] = useState(0);
    const [isRecommend, setIsRecommend] = useState(0);    // isRecommend가 0인 경우 일반적인 포폴 카드 리스트 확인, 1인 경우 추천 포폴 확인 -> 추천에서 페이지네이션 안보이게 하기 위함

    const [sustain, setSustain] = useState(0);
    const [showRecommend, setShow] = useState(0);
    const [recommendedData, setRecommendedData] = useState([]);

    const pageSize = 9;

    // 키워드를 치는 순간 순간마다 연관 검색어 값을 백엔드에서 받아옴
    useEffect(() => {
        if(!selectedIsRecommededPortfolioView){
            console.log('현재 검색된 키워드: ', currentSearchTerm);
            setRelatedSearchTermEnable(true); // 연관 검색어 렌더링 활성화
            fetchFilteredSearchLists();
        }
 
    }, [currentSearchTerm]);

    // <Button> PortfolioCard 다시 눌렀을 때 실행
    // Handler : handleReload() 에 의해 호출됨


    useEffect(() => {
        console.log("selectedIsRecommededPortfolioView : ", selectedIsRecommededPortfolioView);
        if(!selectedIsRecommededPortfolioView){
            setCurrentPage(0);
            setTotalPages(0);
            setSearchTerm("");
            setSelectedBanners(['all']);
            dispatch(setRecommendPortfolioView(false));
            dispatch(saveRecommendedList(null));
            setIsRecommededPortfolioView(false);
            setSaveRecommendedList(null);
            // REQUEST FUNCTION 
            fetchUsers();
            setReload(0);
        }
        
    }, [reload]);


    //추천 버튼 누를 때마다 
    useEffect(() => {
        if (recommend === 1) {
            Recommend();
            setRecommend(0);
            setSustain(1);
            dispatch(setRecommendPortfolioView(true));
            setIsRecommededPortfolioView(true);
        }
        else {
        }
    }, [recommend])


    // 검색 조건이 바뀔 때 실행
    // Handler : toggleBanner / handleSearch, toggleBanner, Pagination / handleSearch
    useEffect(() => {

            console.log('현재 선택된 배너 정보', selectedBanners);
            console.log('현재 검색 완료된 키워드: ', searchTerm);
            fetchUsers();
        
    }, [selectedBanners, currentPage, sortOption, searchTerm]);

    useEffect(() => {
            const recommendedList = selectedRecommendedList;
    
            console.log("-----------------", recommendedList);
    
            setRecommendedData(recommendedList);
    
            console.log("세팅된 데이터=================", recommendedList);
    
            dispatch(setRecommendPortfolioView(true));
            setIsRecommededPortfolioView(true);
    }, [selectedRecommendedList]);



    // 백엔드에 연관 검색어에 기반한 닉네임 값을 받아오기 위한 요청 보내기
    const fetchFilteredSearchLists = async () => {
        try {
            // 만약 검색어가 있다면,
            if (currentSearchTerm !== "") {
                const queryParams = new URLSearchParams({
                    searchTerm: currentSearchTerm, // 검색어 세팅
                });

                // 백엔드에서 데이터 받아오기
                const response = await request('GET', `/getFilteredSearchLists?${queryParams}`);

                // 데이터가 있다면 세팅, 없으면 각각 빈 배열로 세팅
                if (response.data) {
                    setSearchData({
                        userSearchDtoList: response.data.userSearchDtoList || [],
                    });
                } else {
                    // Handle the case where response.data.content is undefined
                    console.error("Error fetching data: response.data.content is undefined");
                }
            } else {
                // 검색어가 없다면, 빈 배열로 세팅
                setSearchData({
                    userSearchDtoList: [],
                });
            }
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    // 너무 긴 제목이나 닉네임이면 적당한 길이로 자르고, ... 붙이기
    const truncateString = (str, maxLength) => {
        if (str.length > maxLength) {
            return str.slice(0, maxLength) + '...';
        }
        return str;
    };

    // 백엔드에서 받아온 연관 검색어(닉네임) 결과를 가지고 실제 렌더링 진행.
    // 유저를 각각 카드로 감싸고, 그 안엔 버튼으로 감쌈
    const renderSection = (title, dataArray) => {

        const handleButtonClick = (title, id, name) => {

            // dispatch(lastVisitedEndpoint('/portfoliocard', '/portfoliocard', '/portfoliocard'));
            // setLastVisitedEndpoint('/portfoliocard');
            // setLastLastVisitedEndpoint('/portfoliocard');
            // setLastLastLastVisitedEndpoint('/portfoliocard');

            // 각각에 대해 올바르게 라우팅 걸어주기
            if (title === 'User') {
                navigate(`/portfolio/${name}`);
            }
        };

        // 빈 배열이 아니라면, 즉, 렌더링해야하는 값임
        if (dataArray && dataArray.length > 0) {
            return (
                <Card size='small' style={{ padding: 0, margin: 0, width: '100%' }}>
                    <div style={{ width: '100%', textAlign: 'left', padding: 0 }}>
                        <strong># {title}</strong>
                    </div>
                    <div style={{ margin: 0 }}>
                        {dataArray.map(item => (
                            <Button
                                key={item.id}
                                type="text"
                                style={{ width: '100%', textAlign: 'left', padding: 0, margin: 0 }}
                                onClick={() => handleButtonClick(title, item.id, item.name)}
                            >
                                {truncateString(item.name, 55)}
                            </Button>
                        ))}
                    </div>
                </Card>
            );
        }
        return null;
    };


    // 백엔드에서 받아온 포트폴리오 정보를 카드로 만들어서 뿌려줌
    const fetchUsers = async () => {
        try {
            const queryParams = new URLSearchParams({ //URLSearchParams 이 클래스는 URL에 대한 쿼리 매개변수를 작성하고 관리하는 데 도움. 'GET' 요청의 URL에 추가될 쿼리 문자열을 만드는 데 사용됨.
                selectedBanners: selectedBanners.join(','), // selectedBanners 배열을 쉼표로 구분된 문자열로 변환
                page: currentPage, //현재 페이지 정보
                size: pageSize, //페이징을 할 크기(현재는 한페이지에 9개씩만 나오도록 구성했음)
                sortOption: sortOption, // 최신 등록순, 조회수 순
                searchTerm: searchTerm // 검색어 키워드 문자열
            });

            const response = await request('GET', `/getCards?${queryParams}`);
            setData(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };



    // 포트폴리오 카드 클릭 핸들러, 해당 유저의 포트폴리오로 이동
    const onClickHandler = (nickName) => {
        // /portfolio/${nickName}로 이동했을 때, 해당 페이지에서 "목록으로 돌아가기" 버튼을 클릭하면,
        // 가장 마지막에 저장한 엔드포인트인 /portfoliocard로 오게끔 dispatch를 통해 lastVisitedEndpoint를 /portfoliocard로 설정
        // dispatch(lastVisitedEndpoint('/portfoliocard', '/portfoliocard', '/portfoliocard'));
        // setLastVisitedEndpoint('/portfoliocard');
        // setLastLastVisitedEndpoint('/portfoliocard');
        // setLastLastLastVisitedEndpoint('/portfoliocard');

        // Error name : Actions must be plain objects. Instead, the actual type was: 'undefined'.
        // Solution : SetLastVisitedEndpoint is not a typical Redux action creator, cannot be stated in dispatch().
        navigate(`/portfolio/${nickName}`);
    }


    // 엔터나 클릭 시에만 변경됨(검색어 관련)
    const handleSearch = (value) => {
        setSustain(0);
        setIsRecommend(0);
        setSearchTerm(value); // 검색어를 세팅
        setRelatedSearchTermEnable(false); // 엔터나 클릭을 눌렀으므로 연관 검색어 렌더링 여부를 false로 설정
        setCurrentPage(0); // 검색어가 바뀌면, 강제로 1페이지로 이동시킴
    };

    // 타이핑 시마다 변경(검색어 관련)
    const handleSearchTerm = (value) => {
        setCurrentSearchTerm(value);

    }


    // <Button> Project의 핸들러, ProjectPage로 이동
    const handleProjectPage = () => {
        setSustain(0);
        navigate('/project');
    };


    // <Button> PortfolioCard 의 핸들러, 페이지 리로딩
    const handleReload = () => {
        dispatch(setRecommendPortfolioView(false));
        dispatch(saveRecommendedList(null));
        setIsRecommededPortfolioView(false);
        setSaveRecommendedList(null);

        setIsRecommend(0);

        setSustain(0);

        setReload(1);
    };


    // <Button> Study의 핸들러, StudyPage로 이동
    const handleStudyPage = () => {
        setSustain(0);
        navigate('/study');
    };


    const toggleBanner = (banner) => {

        setSustain(0);
        setIsRecommend(0);
        if (banner === 'all') { // 만약 선택된 배너가 전체라면 selectedBanners: [all]
            setSelectedBanners(['all']);
        }
        else if (selectedBanners.includes('all')) { // 만약 '전체' 상태에서 '전체'가 아닌 다른 버튼을 눌렀다면, [all] -> [특정 배너]
            setSelectedBanners([banner]);
        }
        else { // 그 외의 경우
            const updatedBanners = selectedBanners.includes(banner) // 만약 활성화된 배너를 다시 클릭했다면 해당 배너를 상태에서 빼줘야함, 만약 비활성화된 배너를 클릭하면 현재 상태에서 지금 클릭한 배너도 현재 상태에 넣어줘야함
                ? selectedBanners.filter((b) => b !== banner)
                : [...selectedBanners, banner];
            // Check if all specific banners are unselected
            const allBannersUnselected = !['web', 'app', 'game', 'ai'].some(b => updatedBanners.includes(b)); // 모든 배너가 제거되어있으면 true , 하나라도 배너가 활성화되어있으면 false

            // If all specific banners are unselected, set selection to "all"
            setSelectedBanners(allBannersUnselected ? ['all'] : updatedBanners); //만약 선택된 배너를 다 비활성화 하면 '전체' 상태로 감
        }

        setCurrentPage(0); // 만약 배너를 다른 걸 고르면 1페이지로 강제 이동시킴
    }


    const Recommend = async () => {
        try {
            const response = await request('GET', `/getRecommendation`);
            setRecommendedData(response.data);
            dispatch(saveRecommendedList(response.data));
            dispatch(setRecommendPortfolioView(true));
            setSaveRecommendedList(response.data);
            setIsRecommededPortfolioView(true);
            window.location.reload();
            setTotalPages(response.data.totalPages);
            console.log(data);
        } catch (error) {
            console.error("레코멘드 노노", error);
        }
    }

    // ... (other code)

    const handleRecommend = () => {
        setIsRecommend(1);
        setShow(1);

        setTimeout(() => {
            setRecommend(1);

            setTimeout(() => {
                setSustain(1);
                setShow(0);
            }, 1000);
        }, 1000);

    }


    // 드롭다운을 위한 코드
    const menu = (
        <Menu selectedKeys={[sortOption]}>
            <Menu.Item key="latestPortfolio" onClick={() => { setSortOption('latestPortfolio'); setIsRecommend(0); setSustain(0); }}>
                최신 등록 순
            </Menu.Item>
            <Menu.Item key="byViewCount" onClick={() => { setSortOption('byViewCount'); setIsRecommend(0); setSustain(0); }}>
                조회수 순
            </Menu.Item>
        </Menu>
    );

    const categoryTagStyle = {
        display: 'flex',
        padding: '0px 5px 0px 5px',
        backgroundColor: '#ff9900', /* 원하는 색상으로 변경 */
        borderRadius: '50px', /* 타원형 모양을 만들기 위해 사용 */
        color: '#1365E6', /* 텍스트 색상 설정 */
        marginLeft: '-0.3%',
        marginRight: '5px'
    };


    // COMPONENTS ###############################################

    const renderContent = () => {

        if (showRecommend === 1) {
            // Show the loading message when data is loading
            return (
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    textAlign: 'center',
                    marginBottom: '20px'
                }}>
                    <div>
                        <strong>알맞는 사람을 찾는중입니다. 잠시만 기다려주세요</strong>
                    </div>
                    <div style={{ marginLeft: '20px' }}>
                        <Spin size="large" />
                    </div>

                </div>
            );
        } else if (sustain === 1) {
            // 포트폴리오가 없는 사람인 경우, 백엔드의 getRecommend함수를 통해 반환되는 data는 null이고, 프론트의 data는 ""로 세팅됨.
            if (data === "") {
                alert("추천 기능을 사용하시려면, 정확한 추천을 위해 먼저 포트폴리오를 작성해주세요!");
            } else {
                // Show the "이런 사람은 어떠세요?" message
                return (
                    <div>
                        <div style={{ textAlign: 'center', marginBottom: '20px', backgroundColor: 'skyblue' }}>
                            <strong>이런 사람은 어떠세요?</strong>
                        </div>
                        <div>
                            {renderCards(recommendedData)}
                        </div>
                    </div>

                );
            }
        } else{
            return renderCards(data, recommendedData);
        }
    };

    // renderCards
    const renderCards = (cards, recommendedCard) => {
        let similarity = null;
        
        if (!cards || cards.length === 0) {
            return <div>No data available</div>; // or any other appropriate message
        }

        if (recommendedCard != null) {
            return (
                <div>
                    <Row gutter={16}>
                        {(recommendedCard ? (recommendedCard.map((item, index) => (
                            <React.Fragment key={index}>
                            <Col xs={24} sm={8} key={index}>
                                {/**<Card onClick={() => onClickHandler(item.nickName)} title={`👩🏻‍💻 ${item.nickName}`} style={{ height: '270px', marginBottom: '10px', cursor: 'pointer' }}>*/}
                                {/* style = {{cursor: 'pointer'}} */}
                                <Card onClick={() => onClickHandler(item.nickName)}
                                    headStyle={{ background: '#e5eefc'}}
                                    bodyStyle={{ paddingTop: '15px', paddingBottom: '15px' }}
                                    title={
                                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <div>
                                                {index === 0 ? <span> <strong style={{fontSize:'20px'}}>Top</strong> recommended </span> : index === 1 ? <span><strong style={{fontSize:'20px'}}>2nd</strong> recommended</span> : <span><strong style={{fontSize:'20px'}}>3rd</strong> recommended</span>}
                                            </div>
                                            <div style={{ display: 'flex', alignItems: 'center' }}>
                                                <span>{index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : ''}</span>
                                            </div>
                                            {/* <span>{item.cosineSimilarity}{index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : ''}</span> */}
                                        </div>
                                    } style={{ height: '270px', marginBottom: '10px', cursor: 'pointer', border: index === 0 ? '1px solid #fee371' : index === 1 ? '1px solid #e6e6e6' : index === 2 ? '1px solid #decba1' : '#e5eefc' }}
                                >
                                    <div style={{display:'grid'}}>
                                        <div style={{display:'flex'}}>
                                            <table style={{width:'90px'}}>
                                                <tbody>
                                                    <tr>
                                                        <td>
                                                            <CircularProgressbarWithChildren 
                                                                value={item.cosineSimilarity}
                                                                styles={buildStyles({
                                                                    strokeLinecap: 'round',
                                                                    pathColor: index === 0 ? `rgba(254, 227, 113, ${item.cosineSimilarity / 100})` : index === 1 ? `rgba(150, 150, 150, ${item.cosineSimilarity / 100})` : index === 2 ? `rgba(222, 203, 161, ${item.cosineSimilarity / 100})` : `rgba(229, 238, 252, ${0})`,
                                                                    textColor: '#f88',
                                                                    trailColor: 'white',
                                                                    backgroundColor: '#3e98c7',
                                                                })}
                                                            >
                                                                <img
                                                                    style={{ borderRadius: '50%', width: '50px', height: '50px', border: `3px solid ${index === 0 ? '#ECC168' : index === 1 ? '#646464' : index === 2 ? '#BC997B' : '#e5eefc'}`}}
                                                                    src={`https://storage.googleapis.com/hongik-pickme-bucket/${item.imageUrl}`}
                                                                />
                                                            </CircularProgressbarWithChildren>
                                                        </td>
                                                    </tr>
                                                    {/* <tr>
                                                        <td>
                                                        <strong>{item.cosineSimilarity}</strong>% similar

                                                        </td>
                                                    </tr> */}
                                                </tbody>
                                            </table>
                                            <div style={{ width: '70%', display: 'grid', marginLeft: '15px'}}>
                                                <div>
                                                    <strong style={{fontSize:'20px'}}> {item.cosineSimilarity}</strong>% similar
                                                </div>
                                                <div style={{ display: 'flex', justifyContent: 'space-between' , marginTop:'2px', marginBottom:'5px'}}>
                                                    <div>
                                                    <strong style={{fontSize:'15px'}}>{item.nickName}</strong>
                                                    </div>
                                                </div>
                                                <div style={{ width: '70px', display: 'flex', flexWrap: 'wrap' }}>
                                                    <strong style={{ display: 'flex', fontSize:'12px' }}>
                                                        {item.web ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#WEB</span> : null}
                                                        {item.app ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#APP</span> : null}
                                                        {item.game ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#GAME</span> : null}
                                                        {item.ai ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#AI</span> : null}
                                                    </strong>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <hr></hr>
                                    <div>
                                        <div>
                                            <b>한 줄 소개</b>
                                            <br></br>
                                            {truncateString(item.shortIntroduce, 20)}
                                        </div>
                                    </div>
                                </Card>
                            </Col>
                            </React.Fragment>))) : (<h2>추천 기능을 사용하시려면, 정확한 추천을 위해 먼저 포트폴리오를 작성해주세요!</h2>)
                        )}
                    </Row>
                </div>
            )
        }


        else {
            return (
                <div>
                    <Row gutter={16}>
                        {cards.map((item, index) => (
                            <Col xs={24} sm={8} key={index}>
                                {/**<Card onClick={() => onClickHandler(item.nickName)} title={`👩🏻‍💻 ${item.nickName}`} style={{ height: '270px', marginBottom: '10px', cursor: 'pointer' }}>*/}
                                {/* style = {{cursor: 'pointer'}} */}
                                <Card 
                                    onClick={() => onClickHandler(item.nickName)} 
                                    headStyle={{ background: '#e5eefc' }} bodyStyle={{ paddingTop: '15px', paddingBottom: '15px' }}
                                    title={
                                        <div style={{ display: 'flex', alignItems: 'center' }}>
                                            <span>
                                                <img
                                                    style={{ borderRadius: '50%', width: '40px', height: '40px', border: '2px solid salmon', marginRight: '10px' }}
                                                    src={`https://storage.googleapis.com/hongik-pickme-bucket/${item.imageUrl}`}
                                                />
                                            </span>

                                            <span>{item.nickName}</span>
                                            {/* <span>{item.cosineSimilarity}</span> */}
                                        </div>
                                        } 
                                    style={{ height: '250px', marginBottom: '10px', cursor: 'pointer' }}
                                >
                                    <b> 관심 분야 선호도 </b>
                                    <div style={{marginTop:'10px'}}>
                                        <strong style={{ display: 'flex', fontSize:'12px' }}>
                                            {item.web ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#WEB</span> : <span ></span>}
                                            {item.app ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#APP</span> : <span ></span>}
                                            {item.game ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#GAME</span> : <span ></span>}
                                            {item.ai ? <span style={{ ...categoryTagStyle, backgroundColor: '#CDF1FF' }}>#AI</span> : <span ></span>}
                                        </strong>
                                    </div>                                    
                                    <Divider style={{ marginTop: '10px', marginBottom: '10px' }}></Divider>
                                    <b> 한 줄 소개 </b>
                                    <br />
                                    {truncateString(item.shortIntroduce, 20)}
                                    <Divider style={{ marginTop: '10px', marginBottom: '10px' }}></Divider>
                                    <b>조회 수 : </b>
                                    {item.viewCount}
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </div>
            )
        }
    }


    return (
        <div>
            {/* 
                검색어 입력 후 엔터/클릭 , 검색어 입력을 할 때마다 바뀌는 이벤트를 별도로 보냄
                handleSearch: 엔터/클릭 관련
                onChange: 동적 타이핑 관련 
            */}
            <br />
            <SearchInPortfolioCardPage onSearch={handleSearch} onChange={handleSearchTerm} />
            {/* 연관 검색어 활성화 여부에 따라 렌더링 진행 */}
            <div style={{ display: 'flex', justifyContent: 'center', textAlign: 'center', margin: '20px 0' }}>
                <div style={{ position: 'absolute', zIndex: 2, width: '55%' }}>
                    {(relatedSearchTermEnable ?
                        (renderSection('User', searchData.userSearchDtoList)) : null)}
                </div>
            </div>
            <div style={{ textAlign: 'center', margin: '20px 0' }}>
                <Row style={{ display: 'flex', justifyContent: 'center' }}>
                    <Button type={selectedBanners.includes('all') ? 'primary' : 'default'}
                        onClick={() => toggleBanner('all')}
                        style={{ marginRight: '10px' }}>
                        All
                    </Button>
                    <Button
                        type={selectedBanners.includes('web') ? 'primary' : 'default'}
                        onClick={() => toggleBanner('web')}>
                        Web
                    </Button>
                    <Button
                        type={selectedBanners.includes('app') ? 'primary' : 'default'}
                        onClick={() => toggleBanner('app')}>
                        App
                    </Button>
                    <Button
                        type={selectedBanners.includes('game') ? 'primary' : 'default'}
                        onClick={() => toggleBanner('game')}>
                        Game
                    </Button>
                    <Button
                        type={selectedBanners.includes('ai') ? 'primary' : 'default'}
                        onClick={() => toggleBanner('ai')}>
                        AI
                    </Button>
                </Row>
            </div>
            <div style={{ textAlign: 'center', marginTop: '15px', marginBottom: '15px' }}>
                <Row>
                    <Col span={18} style={{ textAlign: 'left' }}>
                        {/** 현재 경로가 localhost:3000/project이면 primary형식으로 버튼 표시, 다른 경로라면 default로 표시 */}
                        <Button type={location.pathname === '/portfoliocard' ? 'primary' : 'default'} onClick={handleReload}>
                            Portfolios
                        </Button>
                        <Button type={location.pathname === '/project' ? 'primary' : 'default'} onClick={handleProjectPage}>
                            Project
                        </Button>
                        <Button type={location.pathname === '/study' ? 'primary' : 'default'} onClick={handleStudyPage}>
                            Study
                        </Button>
                    </Col>
                    <Col span={6} style={{ textAlign: 'right' }}>
                        <Dropdown overlay={menu} placement="bottomRight">
                            <Button>
                                정렬
                            </Button>
                        </Dropdown>
                    </Col>
                </Row>
                <hr />
            </div>
            <div style={{
                display: 'flex',
                alignItems: 'center',
                textAlign: 'center',
                marginBottom: '20px'
            }}>
                <div >
                    <Button onClick={() => handleReload()}>
                        전체 보기
                    </Button>
                </div>
                <div >
                    <Button onClick={() => handleRecommend()}>
                        팀원 추천
                    </Button>
                </div>
                <div style={{ marginLeft: '20px' }}>
                    ⬅️ 팀원을 추천받아 보세요!
                </div>
            </div>
            <div style={{ display: 'grid' }}>
                {renderContent()}
            </div>
            {/** 일반적인 포폴 카드 페이지에서는 Pagination이 보이도록, 추천 페이지에서는 Pagination이 보이지 않도록 함 */}
            {recommendedData == null  ? (
                <div style={{ textAlign: 'center', margin: '20px 0' }}>
                    <Pagination
                        current={currentPage + 1} // Ant Design's Pagination starts from 1, while your state starts from 0
                        total={totalPages * pageSize}
                        pageSize={pageSize}
                        onChange={(page) => setCurrentPage(page - 1)} //사용자가 해당 버튼 (예: 2번 버튼)을 누르면 currentPage를 1로 세팅하여 백엔드에 요청 보냄(백엔드는 프런트에서 보는 페이지보다 하나 적은 수부터 페이징을 시작하므로)
                        showSizeChanger={false}
                    />
                </div>
            ) : (
                <div />
            )}
        </div>
    );
}

export default PortfolioCardPage;