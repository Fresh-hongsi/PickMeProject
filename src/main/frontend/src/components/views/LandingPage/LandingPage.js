import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Row, Col, Button, Card, Carousel } from 'antd';
import { useSelector, useDispatch } from 'react-redux';
import { useState, useEffect } from "react";
import PortfolioCard from './Sections/PortfolioCard';
import ProjectCard from './Sections/ProjectCard';
import StudyCard from './Sections/StudyCard';
import SearchInLandingPage from './SearchInLandingPage';
import { request } from '../../../hoc/request';
import { setSaveRecommendedList, setIsRecommededPortfolioView } from "../../../hoc/request";
import { saveRecommendedList, setRecommendPortfolioView } from "../../../_actions/actions";
//import { setLastVisitedEndpoint, setLastLastVisitedEndpoint, setLastLastLastVisitedEndpoint } from '../../../hoc/request';
//import { lastVisitedEndpoint } from '../../../_actions/actions';
import './LandingPage.css';

function LandingPage() {
    // Use useSelector to access isAuthenticated state from Redux store
    //(시홍 뇌피셜: index.js에서 프론트엔드 전역적으로 관리하는 provider태그 안에 store을 넣어줬고,
    //store.js에서 인증과 토큰에 대한 상태 관리를 맡고 있는데,
    //useSelector을 redux로부터 import한 후 갖고 오고 싶은 state를 갖고 올 수 있는듯 하다)

    const navigate = useNavigate();
    const dispatch = useDispatch();

    const [hotPostColor, sethotPostColor] = useState("black");

    const isAuthenticated = useSelector(state => state.auth.isAuthenticated);
    const userRole = useSelector(state => state.auth.userRole);

    // Carousel에 들어갈 배너 이미지
    const images = [
        'https://storage.googleapis.com/hongik-pickme-bucket/Pickme.png',
        'https://storage.googleapis.com/hongik-pickme-bucket/Portfolio.png',
        'https://storage.googleapis.com/hongik-pickme-bucket/Project.png',
        'https://storage.googleapis.com/hongik-pickme-bucket/Study.png'
    ];
    const [hotPost, setHotPost] = useState([]);
    // 백엔드에서 받은 검색어 기반 결과 리스트(3개)를 정의. 처음에 이 페이지에 들어오면 빈 배열
    const [data, setData] = useState({
        projectSearchDtoList: [], // 프로젝트 제목 관련 최대 5개 가져옴
        studySearchDtoList: [], // 스터디 제목 관련 최대 5개 가져옴
        userSearchDtoList: [], // 유저 이름 관련 최대 5개 가져옴
    });
    const [searchTerm, setSearchTerm] = useState(""); //랜딩페이지 내의 검색어 키워드 입력값


    useEffect(() => {
        // 모든 유저에게 랜딩페이지 허용
        if ((isAuthenticated && userRole === 'ADMIN') || (isAuthenticated && userRole === 'USER' || !isAuthenticated)) {
            getHotPost();
            dispatch(setRecommendPortfolioView(false));
            dispatch(saveRecommendedList(null));
            setIsRecommededPortfolioView(false);
            setSaveRecommendedList(null);
        }
    }, [isAuthenticated, userRole]);

    const getHotPost = async () => {
        try {
            const response = await request('GET', '/getHotPost');
            //const response = await axios.get('/getHotPost'); // request가 아닌 axios를 통해 GET 요청을 보냄. 로그인 하지 않은 유저도 볼 수 있어야 하기 때문.

            if (response) {
                setHotPost(response.data);
                console.log("hot post : ", hotPost);
            } else {
                console.error("Error fetching data: getHotPost response.data is undefined");
            }
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }

    // 키워드를 치는 순간 순간마다 백엔드에서 데이터 받아옴
    useEffect(() => {
        console.log('현재 검색된 키워드: ', searchTerm);
        fetchFilteredSearchLists();
    }, [searchTerm]);

    // 백엔드에 검색어 요청 보내기
    const fetchFilteredSearchLists = async () => {
        try {
            // 만약 검색어가 있다면,
            if (searchTerm !== "") {
                const queryParams = new URLSearchParams({
                    searchTerm: searchTerm, // 검색어 세팅
                });

                // 백엔드에서 데이터 받아오기
                const response = await request('GET', `/getFilteredSearchLists?${queryParams}`);

                // 데이터가 있다면 세팅, 없으면 각각 빈 배열로 세팅
                if (response.data) {
                    setData({
                        projectSearchDtoList: response.data.projectSearchDtoList || [],
                        studySearchDtoList: response.data.studySearchDtoList || [],
                        userSearchDtoList: response.data.userSearchDtoList || [],
                    });
                } else {
                    // Handle the case where response.data.content is undefined
                    console.error("Error fetching data: response.data.content is undefined");
                }
            } else {
                // 검색어가 없다면, 빈 배열로 세팅
                setData({
                    projectSearchDtoList: [],
                    studySearchDtoList: [],
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

    // 배너 클릭시 about페이지로 이동
    const handleImageClick = (index) => {
        // 이미지를 클릭할 때 index에 따라 다르게 navigate
        switch (index) {
            case 0:
                navigate('/about');
                break;
            case 1:
                navigate('/portfoliocard');
                break;
            case 2:
                navigate('/project');
                break;
            case 3:
                navigate('/study');
                break;
            default:
                break;
        }
    };

    // 인기 게시물 카드 클릭 시 게시물로 이동
    const onClickHandler = (postType, id) => {
        // 버튼을 클릭하면, 현재 위치를 다 '/'로 세팅해서 디스패치
        // dispatch(lastVisitedEndpoint('/', '/', '/'));
        // setLastVisitedEndpoint('/');
        // setLastLastVisitedEndpoint('/');
        // setLastLastLastVisitedEndpoint('/');

        // 각각에 대해 올바르게 라우팅 걸어주기
        if (postType === 'PROJECT') {
            navigate(`/project/detail/${id}`);
        } else if (postType === 'STUDY') {
            navigate(`/study/detail/${id}`);
        }
    }

    // 2023826 -> 2023년 8월 26일 형식으로 변환
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = date.getMonth() + 1; // Month is zero-based
        const day = date.getDate();
        return `${year}년 ${month}월 ${day}일`;
    };

    // 검색어가 새로이 입력되거나 변경될때마다 여기서 감지해서 백엔드에 보낼 searchTerm을 세팅함
    const handleSearch = (value) => {
        setSearchTerm(value); // 검색어를 세팅
        // console.log('검색어', searchTerm);
    };

    const categoryTagStyle = {
        display: 'flex',
        padding: '0px 5px 0px 5px',
        backgroundColor: '#ff9900', /* 원하는 색상으로 변경 */
        borderRadius: '50px', /* 타원형 모양을 만들기 위해 사용 */
        color: '#677779', /* 텍스트 색상 설정 */
        marginLeft: '-0.3%',
        marginRight: '5px'
    };

    // 백엔드에서 받아온 검색 결과를 가지고 실제 렌더링 진행.
    // 프로젝트, 스터디, 유저를 각각 카드로 감싸고, 그 안엔 버튼으로 감쌈
    const renderSection = (title, dataArray) => {

        const handleButtonClick = (title, id, name) => {

            // 버튼을 클릭하면, 현재 위치를 다 '/'로 세팅해서 디스패치
            // dispatch(lastVisitedEndpoint('/', '/', '/'));
            // setLastVisitedEndpoint('/');
            // setLastLastVisitedEndpoint('/');
            // setLastLastLastVisitedEndpoint('/');

            // 각각에 대해 올바르게 라우팅 걸어주기
            if (title === 'Project') {
                navigate(`/project/detail/${id}`);
            } else if (title === 'Study') {
                navigate(`/study/detail/${id}`);
            } else if (title === 'User') {
                navigate(`/portfolio/${name}`);
            }
        };

        // 빈 배열이 아니라면, 즉, 렌더링해야하는 값임
        if (dataArray && dataArray.length > 0) {
            return (


                <Card size='small' style={{ padding: 0, margin: 0}}>
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

    return (
        <div style={{width:'100%'}}>
            {/* Conditional rendering based on authentication status */}
            {(!isAuthenticated || isAuthenticated && userRole === 'USER') && ( //비회원과 회원이 볼 수 있는 화면
                // Row, Col : 그리드(창의 크기에 맞춘 반응형)를 위해 사용되는 애.

                //  gutter : Row의 열 사이의 간격을 지정함.
                // [가로, 세로]라는 두 개의 값을 갖는 배열임.
                // gutter={[16, 16]}는 열 사이의 가로 및 세로 간격을 각각 16픽셀로 설정
                // 즉, 세로로 따지면 <br/>을 사용하지 않고도, Col 간의 간격이 알아서 16px로 설정됨.

                // span : Col 구성 요소가 확장되어야 하는 열 수를 지정함.
                // 그리드 레이아웃의 총 열 수는 일반적으로 24개.
                // 따라서 span={8}을 설정하면 열이 사용 가능한 너비의 1/3 (8/24)을 차지한다는 의미
                <div>
                    <Carousel autoplay>
                        {images.map((imageUrl, index) => (
                            <div key={index} style={{ display: 'flex', justifyContent: 'center' }}>
                                <img
                                    src={imageUrl}
                                    style={{ width: '100%', height: '100%', cursor: 'pointer' }}
                                    onClick={() => handleImageClick(index)}
                                />
                            </div>
                        ))}
                    </Carousel>
                    <br />
                    <br />
                    <br />
                    <div style={{ marginLeft: '15%', marginRight: '15%' }}>
                        <Row gutter={[16, 16]}>
                            <Col span={24}>
                                <SearchInLandingPage onSearch={handleSearch} />
                            </Col>
                            <Col span={24} style={{ display: 'flex', justifyContent: 'center', textAlign: 'center' }}>

                                <div style={{ position: 'absolute', zIndex: 10, width: '79%' }}>

                                    {renderSection('User', data.userSearchDtoList)}
                                    {renderSection('Project', data.projectSearchDtoList)}
                                    {renderSection('Study', data.studySearchDtoList)}

                                </div>

                            </Col>
    
                            <Col span={24}>
                                <br />
                                <b style={{ fontSize: '20px' }}>🔥 오늘의 인기글</b>
                                <br />
                                <br />
                                <Carousel autoplay slidesToShow={4} dots={false} style={{ marginLeft: '1.25%' }}>
                                    {hotPost.map((item) => {
                                        let hotPostColor;
                                        let TagStyle;
                                        let borderColor;
                                    
                                        if (item.postType === 'PROJECT') {
                                            hotPostColor = 'white';
                                            borderColor = '1px solid #FFEFAA'
                                            TagStyle = {
                                                display: 'flex',
                                                padding: '0px 5px 0px 5px',
                                                backgroundColor: 'lightyellow', /* 원하는 색상으로 변경 */
                                                borderRadius: '50px', /* 타원형 모양을 만들기 위해 사용 */
                                                color: '#ff8400', /* 텍스트 색상 설정 */
                                                marginLeft: '-0.3%',
                                                marginRight: '6px'
                                            };
                                        } else {
                                            hotPostColor = '#FFFBFE';
                                            borderColor = '1px solid #fee5eb'
                                            TagStyle = {
                                                display: 'flex',
                                                padding: '0px 5px 0px 5px',
                                                backgroundColor: '#fee5eb', /* 원하는 색상으로 변경 */
                                                borderRadius: '50px', /* 타원형 모양을 만들기 위해 사용 */
                                                color: '#ff4646', /* 텍스트 색상 설정 */
                                                marginLeft: '-0.3%',
                                                marginRight: '6px',
                                            };
                                        }
                                        
                                        return (
                                        <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <Card onClick={() => onClickHandler(item.postType, item.id)} size="small"
                                                style={{
                                                    cursor: 'pointer', width: '95%', height: '150px', paddingLeft: '3%', paddingRight: '3%',
                                                    border: '0.5px solid skyblue',  borderRadius: '8px',
                                                    backgroundColor:hotPostColor,
                                                    border:borderColor
                                
                                                }}>
                                                <b>{truncateString(item.title, 15)}</b>
                                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                                                    <strong style={{ display: 'flex', fontSize:'12px' }}>

                                                        {item.web ? <span style={{ ...TagStyle}}>#WEB</span> : ""}
                                                        {item.app ? <span style={{ ...TagStyle}}>#APP</span> : ""}
                                                        {item.game ? <span style={{ ...TagStyle}}>#GAME</span> : ""}
                                                        {item.ai ? <span style={{ ...TagStyle }}>#AI</span> : ""}
                                                    </strong>
                                                    <div>

                                                    </div>
                                                </div>
                                                <br />
                                                <div>
                                                    마감일 | {formatDate(item.endDate)}
                                                </div>
                                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                                    <div className="shape-outline mb-1">
                                                        인원 | {item.counts} / {item.recruitmentCount}
                                                    </div>
                                                    <div className="shape-outline mb-1">
                                                        조회수 {item.viewCount}
                                                    </div>
                                                </div>
                                            </Card>
                                        </div>
                                        )
                                    })}
                                </Carousel>
                                <br />
                                <br />
                            </Col>
                            {/* <Col span={24} >
                                <div style={{ textAlign: 'center' }}>
                                    <b style={{ fontSize: '20px' }}>Enjoy your projects with P!ck Me</b>
                                    <br />

                                </div>
                            </Col> */}
                            <Col span={24}>
                                <b style={{ fontSize: '20px', marginBottom: '5px' }}>🔘 바로가기</b>
                            </Col>
                            <Col xs={24} sm={8}>
                                <PortfolioCard />
                            </Col>
                            <Col xs={24} sm={8}>
                                <ProjectCard />
                            </Col>
                            <Col xs={24} sm={8}>
                                <StudyCard />
                            </Col>
                        </Row>
                    </div>
                </div>
            )}
            {isAuthenticated && userRole === 'ADMIN' && ( //인증되었고, 관리자만 볼 수 있는 화면
                // Row, Col : 그리드(창의 크기에 맞춘 반응형)를 위해 사용되는 애.

                //  gutter : Row의 열 사이의 간격을 지정함.
                // [가로, 세로]라는 두 개의 값을 갖는 배열임.
                // gutter={[16, 16]}는 열 사이의 가로 및 세로 간격을 각각 16픽셀로 설정
                // 즉, 세로로 따지면 <br/>을 사용하지 않고도, Col 간의 간격이 알아서 16px로 설정됨.

                // span : Col 구성 요소가 확장되어야 하는 열 수를 지정함.
                // 그리드 레이아웃의 총 열 수는 일반적으로 24개.
                // 따라서 span={8}을 설정하면 열이 사용 가능한 너비의 1/3 (8/24)을 차지한다는 의미
                <div>
                    <Carousel autoplay>
                        {images.map((imageUrl, index) => (
                            <div key={index} style={{ display: 'flex', justifyContent: 'center' }}>
                                <img
                                    src={imageUrl}
                                    style={{ width: '100%', height: '100%' }}
                                />
                            </div>
                        ))}
                    </Carousel>
                    <br />
                    <br />
                    <br />
                    <div style={{ marginLeft: '15%', marginRight: '15%' }}>
                        <Row gutter={[16, 16]}>
                            <Col span={24}>
                                <SearchInLandingPage onSearch={handleSearch} />
                            </Col>
                            <Col span={24} style={{ display: 'flex', justifyContent: 'center', textAlign: 'center' }}>

                                <div style={{ position: 'absolute', zIndex: 10, width: '79%' }}>

                                    {renderSection('User', data.userSearchDtoList)}
                                    {renderSection('Project', data.projectSearchDtoList)}
                                    {renderSection('Study', data.studySearchDtoList)}

                                </div>

                            </Col>
                            <Col span={24}>
                                <br />
                                <b style={{ fontSize: '20px' }}>🔥 오늘의 인기글</b>
                                <br />
                                <br />
                                <Carousel autoplay slidesToShow={4} dots={false} style={{ marginLeft: '1.25%' }}>
                                    {hotPost.map((item) => (
                                        <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <Card onClick={() => onClickHandler(item.postType, item.id)} size="small"
                                                style={{
                                                    cursor: 'pointer', width: '95%', height: '150px', paddingLeft: '3%', paddingRight: '3%',
                                                    border: '1px solid #e8e8e8', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', borderRadius: '8px'
                                                }}>
                                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                                    <strong style={{ display: 'flex' , fontSize:'12px'}}>

                                                        {item.web ? <span style={{ ...categoryTagStyle, backgroundColor: '#91e2c3' }}>#WEB</span> : ""}
                                                        {item.app ? <span style={{ ...categoryTagStyle, backgroundColor: '#91e2c3' }}>#APP</span> : ""}
                                                        {item.game ? <span style={{ ...categoryTagStyle, backgroundColor: '#91e2c3' }}>#GAME</span> : ""}
                                                        {item.ai ? <span style={{ ...categoryTagStyle, backgroundColor: '#91e2c3' }}>#AI</span> : ""}
                                                    </strong>
                                                    <div>

                                                    </div>
                                                </div>
                                                <b>{truncateString(item.title, 15)}</b>
                                                <br />
                                                <br />
                                                <div>
                                                    마감일 | {formatDate(item.endDate)}
                                                </div>
                                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                                    <div className="shape-outline mb-1">
                                                        인원 | {item.counts} / {item.recruitmentCount}
                                                    </div>
                                                    <div className="shape-outline mb-1">
                                                        조회수 {item.viewCount}
                                                    </div>
                                                </div>
                                            </Card>
                                        </div>
                                    ))}
                                </Carousel>
                                <br />
                                <br />
                            </Col>
                            <Col span={24}>
                                <b style={{ fontSize: '20px' }}>🔘 게시판 이동</b>
                                <br />
                            </Col>
                            <Col xs={24} sm={8}>
                                <PortfolioCard />
                            </Col>
                            <Col xs={24} sm={8}>
                                <ProjectCard />
                            </Col>
                            <Col xs={24} sm={8}>
                                <StudyCard />
                            </Col>
                        </Row>
                    </div>
                </div>
            )}
        </div>
    );
}

export default LandingPage;