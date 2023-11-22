import { React, useState, useEffect } from 'react';
import { Layout, Button, Drawer, Card, message, Modal } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { setAuthHeader, setUserRole } from '../../../hoc/request';
import { useSelector, useDispatch } from 'react-redux';
import { request } from '../../../hoc/request';
import { logout } from '../../../_actions/actions'
import { setSaveRecommendedList, setIsRecommededPortfolioView } from "../../../hoc/request";
import { saveRecommendedList, setRecommendPortfolioView } from "../../../_actions/actions";
//import { lastVisitedEndpoint } from '../../../_actions/actions';
//import { setLastVisitedEndpoint, setLastLastVisitedEndpoint, setLastLastLastVisitedEndpoint } from '../../../hoc/request';
import CustomDropdown from './Sections/CustomDropdown';
import { CloseOutlined } from '@ant-design/icons';
//import Notifications from '../../utils/Notifications';

const { Header } = Layout;
//const { Title } = Typography;

function MyHeader(props) { //여기서 props는 로고 모양을 app.js에서 가져오기 위함
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const location = useLocation();
    const currentEndpoint = location.pathname;
    const isAuthenticated = useSelector(state => state.auth.isAuthenticated);
    const userRole = useSelector(state => state.auth.userRole);
    //const visitedEndEndEndpoint = useSelector(state => state.endpoint.lastLastLastVisitedEndpoint);

    const [open, setOpen] = useState(false);
    const [notificationData, setNotificationData] = useState([]);
    const [deleteReadModalVisible, setDeleteReadModalVisible] = useState(false);    // 읽은 알림 삭제 관련 모달
    const [deleteAllModalVisible, setDeleteAllModalVisible] = useState(false);      // 모든 알림 삭제 관련 모달
    //const [notReadCount, setNotReadCount] = useState(0);        // 읽지 않은 알림 개수

    // 최소화 문제를 해결하기 위한 애
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);

    // useEffect를 사용하여 윈도우 크기 변경 시 state 업데이트
    useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };
    
        window.addEventListener('resize', handleResize);
    
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

  
    // useEffect(() => {
    //     getNotReadCount();  // 읽지 않은 알림 수 가져오기
    // }, []);

    // // 읽지 않은 알림 수 가져오기
    // const getNotReadCount = () => {
    //     request('GET', 'sse/getNotReadCount', {})
    //         .then((response) => {
    //             setNotReadCount(response.data);     // async await하면 데이터를 못가져오더라
    //             console.log('알림', response.data);
    //         })
    //         .catch((error) => {
    //             console.log("Error fetching data:", error);
    //         })
    // }

    // Notification 배너가 열리면 해당 회원의 모든 알림을 가져와서 렌더링
    const showDrawer = () => {
        setOpen(true);

        // 백엔드에 모든 알림 가져오기 요청
        request('GET', 'sse/getNotifications', {})
            .then((response) => {
                setNotificationData(response.data);
                console.log('알림', response.data);
            })
            .catch((error) => {
                console.log("Error fetching data:", error);
            })
    };

    // 알림 배너 닫기
    const onClose = () => {
        setOpen(false);
    };


    const handleScrap = () => {
        dispatch(setRecommendPortfolioView(false));
        dispatch(saveRecommendedList(null));
        setIsRecommededPortfolioView(false);
        setSaveRecommendedList(null);
        navigate('/scrap');
    }

    const handlePortfolio = () => {
        dispatch(setRecommendPortfolioView(false));
        dispatch(saveRecommendedList(null));
        setIsRecommededPortfolioView(false);
        setSaveRecommendedList(null);
        navigate('/portfolio');
    }

    const handleGroup = () => {
        dispatch(setRecommendPortfolioView(false));
        dispatch(saveRecommendedList(null));
        setIsRecommededPortfolioView(false);
        setSaveRecommendedList(null);
        navigate('/group');
    }


    const showDeleteReadModal = () => {
        setDeleteReadModalVisible(true);
    };
      
    const hideDeleteReadModal = () => {
        setDeleteReadModalVisible(false);
    };
    
    // 읽은 알림 삭제
    const confirmDeleteRead = () => {
        // 백엔드에 읽은 알림 삭제 요청
        request('POST', 'sse/deleteNotification/read', {})
            .then((response) => {
                message.success('알림이 삭제되었습니다.');
                // 읽지 않은 알림들만 notificationData로 세팅
                setNotificationData(response.data);
            })
            .catch((error) => {
                console.log("Error fetching data:", error);
                message.error('알림 삭제에 실패했습니다.');
            });

        // // 읽지 않은 알림 수 가져오기
        // getNotReadCount();

        hideDeleteReadModal();
    };
      
    const showDeleteAllModal = () => {
        setDeleteAllModalVisible(true);
    };
      
    const hideDeleteAllModal = () => {
        setDeleteAllModalVisible(false);
    };
    
    // 전체 알림 삭제
    const confirmDeleteAll = () => {
        // 백엔드에 전체 알림 삭제 요청
        request('POST', 'sse/deleteNotification/all', {})
            .then((response) => {
                message.success('알림이 삭제되었습니다.');
                // 전체 알림이 삭제되었으므로, notificationData는 null로 세팅
                setNotificationData([]);
            })
            .catch((error) => {
                console.log("Error fetching data:", error);
                message.error('알림 삭제에 실패했습니다.');
            });

        // // 읽지 않은 알림 수 가져오기
        // getNotReadCount();
        
        hideDeleteAllModal();
    };


    //로그아웃 버튼을 클릭하면 호출되며, 로컬 스토리지의 토큰을 삭제하고 로그아웃 액션을 디스패치합니다.
    //즉 로그 아웃 버튼 누르면 로컬 스토리지의 'auth-token'필드를 null 로 채우고, action.js에 등록된 logout관련 액션을 수행하도록 dispatch(강제 명령) 날림. 그리고 그 상태 값이 store.js의 switch문에 의해 변경됨
    const handleLogout = () => {
        setAuthHeader(null); // Clear token in local storage(로컬 스토리지에서 토큰 지우는 건 인증받지 못한 사람은 api호출을 못하게 하기 위함)
        setUserRole(null); //로컬 스토리지에서 역할을 지우는건 역할 없는 사람은 api호출을 못하게 하기 위함
        localStorage.clear();   // 로컬스토리지 클리어 반드시 해주기!! 얘 안하면 로그아웃 상태에서 새로고침 시 랜딩페이지가 렌더링되지 않음!!
        dispatch(logout()); // Dispatch logout action(이걸 하는 이유는 프런트 전역적으로 이 사람이 인증받지 못한 사람이란 걸 인지하게 하기 위함) 

        //여기서 navigate(/)해줘야할듯? -> 로그아웃 버튼 누르면 홈페이지로 가는게 맞을 것 같다
        navigate('/');
    };

    const login = () => {
        navigate('/login');
        console.log("login");
    };

    const handleLogoClick = () => { //로고 클릭하면 홈 화면으로 다시 라우팅
        console.log("go home by site logo");
        // dispatch(lastVisitedEndpoint('/', '/', '/'));
        // setLastVisitedEndpoint('/');
        // setLastLastVisitedEndpoint('/');
        // setLastLastLastVisitedEndpoint('/');

        dispatch(setRecommendPortfolioView(false));
        dispatch(saveRecommendedList(null));
        setIsRecommededPortfolioView(false);
        setSaveRecommendedList(null);
       
        navigate('/');
    };

    const CustomTitle = () => (
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
                알림
            </div>
            <div>
                <Button
                    type="primary"
                    value="small"
                    style={{ marginRight: '3px' }}
                    onClick={showDeleteReadModal}
                >
                    읽은 알림 삭제
                </Button>
                <Button
                    type="primary"
                    value="small"
                    onClick={showDeleteAllModal}
                >
                    전체 알림 삭제
                </Button>
            </div>
        </div>
    );

    // 알림 카드 각각을 클릭했을 때 동작
    const handleCardClick = (postId, postType, notificationId) => {


        // 알림을 읽으면, Notifications table의 checked를 true로 바꾸기 위해 put request 전송
        request('PUT', `sse/checkNotification/${notificationId}`, {})
            .then((response) => {
                console.log("알림을 읽었습니다.");
                dispatch(setRecommendPortfolioView(false));
                dispatch(saveRecommendedList(null));
                setIsRecommededPortfolioView(false);
                setSaveRecommendedList(null);
            })
            .catch((error) => {
                console.log("Error fetching data:", error);
                message.error('데이터베이스에서 checked를 true로 바꾸는데 실패했습니다.');
            });

        // Notification Drawer 창 닫기
        onClose();
        
        // 디테일 페이지에서 알림 클릭 시, 목록으로 안돌아가지는 문제 해결을 위한 애.
        if (!currentEndpoint.startsWith("/project/detail/") && !currentEndpoint.startsWith("/study/detail/")) {
            // dispatch(lastVisitedEndpoint(currentEndpoint, currentEndpoint, visitedEndEndEndpoint));    // 전역에 상태 저장을 위한 
            // setLastVisitedEndpoint(currentEndpoint);   // 새로고침 문제를 해결하기 위한 애. 로컬스토리지에 저장.
            // setLastLastVisitedEndpoint(currentEndpoint);
            // setLastLastLastVisitedEndpoint(visitedEndEndEndpoint);
        }

        
        const lowerType = postType.toLowerCase(); // 백엔드에서 받은 postType은 PROJECT , STUDY와 같은 형식이므로 navigate를 위해선 소문자로 바꿔줄 필요가 있음

        if (currentEndpoint === `/${lowerType}/detail/${postId}`) {
            message.warning('이동하려는 페이지가 현재 보고있는 페이지입니다.');
        }

        navigate(`${lowerType}/detail/${postId}`); // 알림에 해당하는 게시물로 navigate 걸어줌

        // // 새 창을 열어서 페이지를 띄우기
        // const newWindow = window.open(`${lowerType}/detail/notify/${postId}`, '_blank');
        // if (newWindow) {
        //     newWindow.opener = null; // 새 창에서 브라우저 열기
        // } else {
        //     message.error('팝업 창을 열 수 없습니다. 팝업 차단 설정을 확인하세요.');
        // }
    };

    // 알림 카드 닫기 버튼 클릭시 호출될 함수
    const handleCardClose = (notificationId) => (e) => {

        e.stopPropagation(); // 상위의 이벤트인 handleCardClick가 실행되는 걸 막음

        // 백엔드에 해당 알림 id를 delete 요청
        request('POST', `sse/deleteNotification/${notificationId}`, {})
            .then((response) => {
                message.success('알림이 삭제되었습니다.');
                // 알림을 삭제한 후, 상태를 업데이트하여 해당 알림을 화면에서 제거
                setNotificationData(prevData => prevData.filter(notification => notification.notificationId !== notificationId));
            })
            .catch((error) => {
                console.log("Error fetching data:", error);
                message.error('알림 삭제에 실패했습니다.');
            });

        // Drawer를 열었다가 닫지 않고, 업데이트된 알림 데이터를 사용하여 카드를 다시 렌더링
        setOpen(true);
    };

    return (
        <div>
            <Header className="App-header" style={{ height: '85px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '100%', paddingLeft: '15%', paddingRight: '15%',  }}>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <img
                            src={props.logoSrc}
                            className="App-logo"
                            alt="logo"
                            onClick={handleLogoClick}
                            style={{ cursor: 'pointer', maxWidth: '200px', maxHeight: '40px' }}
                        />
                    </div>
                    <div>
                        {/** 토글 형식, background: 'transparent' : 버튼 배경을 투명하게, padding: '20px 40px' : 각각 Top, Bottom 패딩 설정 */}
                        {isAuthenticated ? (
                            <div>
                                {windowWidth > 1000 ? ( // 1000px 넘을 경우, 모든 헤더 보여줌
                                <div>
                                <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px', height: '10vh', }} onClick={showDrawer}>
                                    Notification {/*notReadCount.notReadCount === 0 ? "" : "(" + notReadCount.notReadCount + ")"*/}
                                </Button>
                                <Drawer title={<CustomTitle />} width={520} closable={false} onClose={onClose} open={open}>
                                    <div>
                                        {/* 알림 데이터를 Card 컴포넌트로 렌더링 */}
                                        {notificationData.length > 0 && (
                                            // 알림 데이터를 Card 컴포넌트로 렌더링
                                            notificationData.map((notification, index) => {
                                                // 만약 알림 내용이 하나도 없으면 렌더링하지 않음
                                                if (
                                                    notification.postId === null &&
                                                    notification.notificationMessage === null &&
                                                    notification.postType === null &&
                                                    notification.notificationId === null &&
                                                    notification.isRead === null
                                                ) {
                                                    return null; // postId, notificationMessage, postType, notificationId이 모두 null인 경우 카드를 렌더링하지 않음
                                                }

                                                // notification.isRead 값에 따라 다른 스타일을 적용
                                                const cardStyle = {
                                                    cursor: 'pointer',
                                                    marginBottom: '10px',
                                                    backgroundColor: notification.isRead ? 'white' : '#ffffdd', // isRead가 false이면 안읽은 게시물로 배경색을 다르게 설정
                                                };

                                                return (
                                                    <Card
                                                        key={index}
                                                        onClick={() => handleCardClick(notification.postId, notification.postType, notification.notificationId)}
                                                        style={cardStyle}
                                                    >
                                                        <Button
                                                            style={{
                                                                position: 'absolute',
                                                                right: '4px',
                                                                top: '4px',
                                                                cursor: 'pointer',
                                                                fontSize: '20px',
                                                                backgroundColor: '#ddddff',
                                                                border: 'none', // 버튼 스타일링을 위해 추가
                                                                display: 'flex', // 아이콘과 텍스트를 가운데 정렬하기 위해 추가
                                                                alignItems: 'center', // 아이콘을 세로 중앙에 정렬하기 위해 추가
                                                                justifyContent: 'center', // 아이콘을 가로 중앙에 정렬하기 위해 추가
                                                            }}
                                                            size="small"
                                                            onClick={handleCardClose(notification.notificationId)}
                                                            icon={<CloseOutlined />} // CloseOutlined 아이콘을 사용하여 X 모양 버튼으로 만듦
                                                        />
                                                        {/* 여기에 알림 내용을 출력 */}
                                                        {notification.notificationMessage}
                                                    </Card>
                                                );
                                            })
                                        )}
                                    </div>
                                </Drawer>
                                <Modal
                                    title="읽은 알림 삭제"
                                    open={deleteReadModalVisible}
                                    onOk={confirmDeleteRead}
                                    onCancel={hideDeleteReadModal}
                                    okText="예"
                                    cancelText="아니오"
                                >
                                    읽은 알림을 모두 삭제하시겠습니까?
                                </Modal>
                                <Modal
                                    title="전체 알림 삭제"
                                    open={deleteAllModalVisible}
                                    onOk={confirmDeleteAll}
                                    onCancel={hideDeleteAllModal}
                                    okText="예"
                                    cancelText="아니오"
                                >
                                    전체 알림을 모두 삭제하시겠습니까?
                                </Modal>
                                <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px', height: '10vh', }} onClick={handleScrap}>
                                    Scrap
                                </Button>
                                <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px', height: '10vh', }} onClick={handlePortfolio}>
                                    Portfolio
                                </Button>
                                <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px', height: '10vh', }} onClick={handleGroup}>
                                    Group
                                </Button>
                                <CustomDropdown userRole={userRole} handleLogout={handleLogout}>
                                    gd
                                </CustomDropdown>
                                </div>
                                ) : (
                                    windowWidth > 600 ? ( // 600px < 가로 길이 <= 1000px 인 경우, 노티피케이션 + 프사 보여줌
                                        <div>
                                            <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px', height: '10vh', }} onClick={showDrawer}>
                                                Notification {/*notReadCount.notReadCount === 0 ? "" : "(" + notReadCount.notReadCount + ")"*/}
                                            </Button>
                                            <Drawer title={<CustomTitle />} width={520} closable={false} onClose={onClose} open={open}>
                                                <div>
                                                    {/* 알림 데이터를 Card 컴포넌트로 렌더링 */}
                                                    {notificationData.length > 0 && (
                                                        // 알림 데이터를 Card 컴포넌트로 렌더링
                                                        notificationData.map((notification, index) => {
                                                            // 만약 알림 내용이 하나도 없으면 렌더링하지 않음
                                                            if (
                                                                notification.postId === null &&
                                                                notification.notificationMessage === null &&
                                                                notification.postType === null &&
                                                                notification.notificationId === null &&
                                                                notification.isRead === null
                                                            ) {
                                                                return null; // postId, notificationMessage, postType, notificationId이 모두 null인 경우 카드를 렌더링하지 않음
                                                            }

                                                            // notification.isRead 값에 따라 다른 스타일을 적용
                                                            const cardStyle = {
                                                                cursor: 'pointer',
                                                                marginBottom: '10px',
                                                                backgroundColor: notification.isRead ? 'white' : '#ffffdd', // isRead가 false이면 안읽은 게시물로 배경색을 다르게 설정
                                                            };

                                                            return (
                                                                <Card
                                                                    key={index}
                                                                    onClick={() => handleCardClick(notification.postId, notification.postType, notification.notificationId)}
                                                                    style={cardStyle}
                                                                >
                                                                    <Button
                                                                        style={{
                                                                            position: 'absolute',
                                                                            right: '4px',
                                                                            top: '4px',
                                                                            cursor: 'pointer',
                                                                            fontSize: '20px',
                                                                            backgroundColor: '#ddddff',
                                                                            border: 'none', // 버튼 스타일링을 위해 추가
                                                                            display: 'flex', // 아이콘과 텍스트를 가운데 정렬하기 위해 추가
                                                                            alignItems: 'center', // 아이콘을 세로 중앙에 정렬하기 위해 추가
                                                                            justifyContent: 'center', // 아이콘을 가로 중앙에 정렬하기 위해 추가
                                                                        }}
                                                                        size="small"
                                                                        onClick={handleCardClose(notification.notificationId)}
                                                                        icon={<CloseOutlined />} // CloseOutlined 아이콘을 사용하여 X 모양 버튼으로 만듦
                                                                    />
                                                                    {/* 여기에 알림 내용을 출력 */}
                                                                    {notification.notificationMessage}
                                                                </Card>
                                                            );
                                                        })
                                                    )}
                                                </div>
                                            </Drawer>
                                            <Modal
                                                title="읽은 알림 삭제"
                                                open={deleteReadModalVisible}
                                                onOk={confirmDeleteRead}
                                                onCancel={hideDeleteReadModal}
                                                okText="예"
                                                cancelText="아니오"
                                            >
                                                읽은 알림을 모두 삭제하시겠습니까?
                                            </Modal>
                                            <Modal
                                                title="전체 알림 삭제"
                                                open={deleteAllModalVisible}
                                                onOk={confirmDeleteAll}
                                                onCancel={hideDeleteAllModal}
                                                okText="예"
                                                cancelText="아니오"
                                            >
                                                전체 알림을 모두 삭제하시겠습니까?
                                            </Modal>
                                            <CustomDropdown userRole={userRole} handleLogout={handleLogout}>
                                                gd
                                            </CustomDropdown>
                                        </div>
                                    ) : (       // 600px보다 가로 길이가 작은 경우, 프사만 보여줌. 어차피 프사에 노티피케이션 말고 다른 버튼들 모두 들어있음
                                        <div>
                                            <CustomDropdown userRole={userRole} handleLogout={handleLogout}>
                                                gd
                                            </CustomDropdown>
                                        </div>
                                    )
                                )}
                            </div>
                        ) : (
                            <Button type="text" value="large" style={{ color: 'black', background: 'transparent', fontSize: '18px' }} onClick={login}>
                                Login
                            </Button>
                        )}
                    </div>
                </div>
            </Header>
        </div>
    );
}

export default MyHeader;
