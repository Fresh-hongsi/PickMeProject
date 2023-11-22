import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Row, Col, Tabs, Input, Button, message } from 'antd';
import { request, setAuthHeader, setHasPortfolio, setUserRole, setUserNickName, setIsRecommededPortfolioView, setSaveRecommendedList } from '../../../hoc/request';
import { loginSuccess, saveRecommendedList, setRecommendPortfolioView } from '../../../_actions/actions'

function LoginPage() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    // 로그인 및 회원가입 폼의 상태를 관리할 state 변수들
    const [active, setActive] = useState('login'); // 로그인과 회원가입 탭을 전환하기 위한 활성 탭 상태
    const [userName, setUserName] = useState(''); // 이름
    const [nickName, setNickName] = useState('');   // 닉네임
    const [email, setEmail] = useState('');         // 사용자 이메일
    const [password, setPassword] = useState('');   // 비밀번호
    const [nicknameAvailability, setNicknameAvailability] = useState(null); //닉네임 중복 여부
    const [mailSented, setMailSented] = useState(false); //인증 메일 발송 여부
    const [userInputCode, setUserInputCode] = useState(''); // 유저가 입력한 이메일 인증번호
    const [verificationSuccess, setVerificationSuccess] = useState(null); // 인증번호 확인 성공 여부
    const fixedEmailDomain = "@g.hongik.ac.kr"; // 회원가입할 때, 유저는 @g.hongik.ac.kr까지 입력하지 않는다. 따라서 이메일 인증 시 또는 회원가입 폼 전체를 제출할 때, 명시적으로 입력받은 email변수에 @g.hongik.ac.kr를 달아줘서 백엔드에 보낸다
    const [isPasswordResetFormVisible, setIsPasswordResetFormVisible] = useState(false); // 비밀번호 찾기 토글 버튼
    const [findPasswordEmail, setFindPasswordEmail] = useState(''); // 비밀번호 찾기에 입력하는 사용자 이메일
    const [resetPasswordMailSented, setResetPasswordMailSented] = useState(false);   // 비밀번호 찾기 인증 메일 발송 여부

    // 입력 필드 변경 시 호출되는 이벤트 핸들러 (로그인, 회원가입 공통으로 관리하고, 입력시마다 변수에 입력값을 세팅함)
    const onChangeHandler = (event) => {
        const name = event.target.name;
        const value = event.target.value;

        // 입력 필드마다 해당하는 state 변수를 업데이트
        if (name === 'userName') setUserName(value);
        else if (name === 'nickName') { //닉네임 필드를 입력할 때
            setNickName(value); //현재 입력 값을 닉네임 변수에 세팅
            setNicknameAvailability(null); //새로운 값을 입력할 떄는 닉네임 사용 가능 여부를 null로 초기화
        }
        else if (name === 'email') setEmail(value);
        else if (name === 'password') setPassword(value);
        else if (name === 'userInputCode') {
            setUserInputCode(value); //현재 사용자가 입력하는 인증번호 값을 인증번호 변수에 세팅
            setVerificationSuccess(null); // 새로운 인증번호를 입력할 떄는 인증 여부를 null로 초기화
        }
        else if (name === 'findPasswordEmail') setFindPasswordEmail(value);
    };

    // 회원 가입 관련 - 중복 확인 버튼을 누르면 호출되는 이벤트 핸들러
    const handleDuplicateCheck = () => {

        if (nickName.length > 10) {
            message.warning('닉네임은 최대 10자까지 입력 가능합니다.');
            return;
        }
        request('GET', `/nicknameDuplicate?nickname=${nickName}`) //백엔드에 현재 입력받은 nickname을 가진 회원이 있는 지 찾고, 백엔드는 해당 닉네임으로 유저 생성 가능하면 available:true /불가능하면 available:false 반환
            .then((response) => {
                const isAvailable = response.data.available;
                setNicknameAvailability(isAvailable); //닉네임 사용 가능 여부 값을 상태변수에 저장
            })
            .catch((error) => {
                alert("잠시 후 다시 시도해보세요.");
            });
    };

    // 회원 가입 관련 - 이메일 인증 버튼을 누르면 호출되는 이벤트 핸들러
    const handleSendVerificationCode = () => {

        // 만약 email 입력하는 창에 아무것도 적지 않고 인증번호 발송 버튼 누르면 백엔드에 요청 안보냄
        if (!email) {
            message.warning('이메일을 입력해주세요.');
            return;
        }

        // 회원 가입 폼에서 email창에 입력받은 변수 ex) qkrtlghd97을 백엔드에 보낼 땐 @g.hongik.ac.kr를 달아줘서 보내야함
        const convertedToEmail = email + fixedEmailDomain;

        // 백엔드에 보낼 email관련 RequestParam 작성
        const emailParams = new URLSearchParams({ email: convertedToEmail });
        console.log('email', convertedToEmail);

        // 백엔드에 이메일 발송 관련 요청 보냄
        request('POST', `/mailConfirm?${emailParams}`)
            .then((response) => {
                if (response.data.sented === true) { // 메일 보내는 데에 성공한 경우
                    message.success('해당 이메일로 인증코드가 발송되었습니다.');
                    setMailSented(true); // 메일 전송 상태변수를 true로 세팅
                    setVerificationSuccess(null); // 인증 완료 여부는 null로 변경(인증 완료됐는데 또 인증 요청 버튼을 눌렀을 떄에 대한 예외처리임)

                } else if (response.data.sented === false) { // 메일 보내는 데에 실패한 경우 - 즉 이미 db에 존재하는 이메일임
                    message.error('이미 등록된 이메일 주소입니다. 다른 유효한 이메일을 입력하세요.');
                }
            })
            .catch((error) => {
                alert("잠시 후 다시 시도하세요.");
            });
    };


    // 회원 가입 관련 - 인증 코드 입력 칸 옆에 '확인' 버튼을 누르면 호출되는 이벤트 핸들러
    const handleVerifyCode = () => {

        // 만약 인증코드 입력 창에 아무것도 적지 않고 확인 버튼 누르면 백엔드에 요청 안보냄
        if (!userInputCode) {
            message.warning('인증 코드를 입력해주세요.');
            return;
        }

        // 회원 가입 폼에서 email창에 입력받은 변수 ex) qkrtlghd97을 백엔드에 보낼 땐 @g.hongik.ac.kr를 달아줘서 보내야함
        const convertedToEmail = email + fixedEmailDomain;

        // 백엔드에 보낼 email, 인증번호 관련 RequestParam 작성
        // 백엔드에서는 인증번호 검증을, redis에 저장되어있는 email이란 key에 해당하는 인증번호 value가 유효한 시간 안에 존재하는 지 확인, 프런트에서 받아온 인증번호가 redis에 저장되어있는 인증번호와 일치하면 verified를 1로 반환
        // 인증 시간 내에 있는데 코드가 틀린 경우: verified ==2 
        // 파기된 인증번호면 verified == 0
        const verifyParams = new URLSearchParams({ email: convertedToEmail, code: userInputCode });
        console.log('email', convertedToEmail);
        console.log('code', userInputCode);

        request('POST', `verifyCode?${verifyParams}`)
            .then((response) => {
                if (response.data.verified === 1) {
                    setVerificationSuccess(1); // 인증 시간 내에, 인증번호가 일치하는 경우 검증완료 상태를 1로 세팅
                }
                else if (response.data.verified === 0) {

                    setVerificationSuccess(0); // 인증 시간이 지난 경우 검증 완료 상태를 0으로 세팅
                }
                else {
                    setVerificationSuccess(2); // 인증 시간 내에 있지만 코드가 틀렸을 경우 검증 완료 상태를 2로 세팅
                }
            })
            .catch((error) => {
                alert("잠시 후 다시 시도하세요.");
            });

    };


    // 로그인 폼 제출 시 호출되는 이벤트 핸들러
    const onSubmitLogin = (e) => {
        e.preventDefault();

        if (!email) {
            message.warning('이메일을 입력해주세요.');
            return;
        }
        if (!password) {
            message.warning('비밀번호를 입력해주세요.');
            return;
        }

        onLogin(e, email, password); // 부모 컴포넌트로부터 전달받은 onLogin 함수 호출
        navigate('/');
    };

    const onLogin = (event, email, password) => {
        event.preventDefault();

        request('POST', '/login', {
            email: email,
            password: password
        })
            .then((response) => {
                const { token, role, isCreated, nickName } = response.data;
                dispatch(loginSuccess(token, role, isCreated, nickName)); // Dispatch login success action with role
                dispatch(setRecommendPortfolioView(false));
                dispatch(saveRecommendedList(null));
                setIsRecommededPortfolioView(false);
                setSaveRecommendedList(null);
                setAuthHeader(token); // Set token in local storage
                setUserRole(role);
                setHasPortfolio(isCreated);
                setUserNickName(nickName);
                localStorage.setItem('localStorageCleared', 'true'); //로컬 스토리지가 비워졌다고 명시. F5문제를 위해 설정한 임시 방편
                alert("로그인에 성공하였습니다.");
            })
            .catch((error) => {
                alert("로그인에 실패하였습니다.");
            });
    };



    // 회원가입 폼 제출 시 호출되는 이벤트 핸들러
    const onSubmitRegister = (e) => {
        e.preventDefault(); //페이지에서 새로고침하면..

        if (!userName) {
            message.warning('이름을 입력해주세요.');
            return;
        }
        if (!nickName) {
            message.warning('닉네임을 입력해주세요.');
            return;
        }
        if (nickName.length > 10) { // Check nickname length here
            message.warning('닉네임은 최대 10자까지 입력 가능합니다.');
            return;
        }
        if (nicknameAvailability === false) { //만약 닉네임 중복인 걸 아는데도 불구하고 회원가입 버튼을 누르는 경우
            message.warning('이미 사용 중인 닉네임입니다. 닉네임 변경 후 다시 시도하세요.');
            return;
        }
        if (nicknameAvailability === null) {
            message.warning('닉네임 중복 확인을 먼저 해주세요.');
            return;
        }
        if (!email) {
            message.warning('이메일을 입력해주세요.');
            return;
        }
        if (verificationSuccess === null) {
            message.warning('이메일 인증 확인이 아직 되지 않았습니다. 이메일 인증 후 다시 시도해주세요.')
            return;
        }
        if (verificationSuccess === 2) {
            message.warning('이메일 인증 코드가 제대로 입력되지 않았습니다. 인증 번호를 다시 입력해주세요.')
            return;
        }
        if (verificationSuccess === 0) {
            message.warning('인증 시간이 만료되었습니다. 인증 번호를 다시 발급받으세요.')
            return;
        }

        if (!password) {
            message.warning('비밀번호를 설정해주세요.');
            return;
        }



        // 부모 컴포넌트로부터 전달받은 onRegister 함수 호출 
        onRegister(e, userName, nickName, email, password);
        navigate('/');
    };

    // Login 컴포넌트 내에서 회원가입 액션을 처리하는 함수를 정의
    const onRegister = (event, userName, nickName, email, password) => {
        event.preventDefault();
        const convertedToEmail = email + fixedEmailDomain; // 이메일에 qkrtlghd97만 들어있는 상태이므로 백엔드의 회원가입 로직 내에서 사용하기 위한 이메일 형태로 만들어줘야함

        request('POST', '/register', {
            userName: userName,
            nickName: nickName,
            email: convertedToEmail,
            password: password
        })
            // 회원가입 성공
            .then((response) => {
                //setAuthHeader(response.data.token);     // 헤더에 토큰 설정   -> 회원가입 시 토큰 설정 배제
                alert("회원가입에 성공하였습니다.");
            })
            // 회원가입 실패
            .catch((error) => {
                //setAuthHeader(null);                    // 헤더에 토큰 지우기 -> 회원가입 시 토큰 설정 배제
                alert("회원가입에 실패하였습니다.");
            });
    };

    // 비밀번호 찾기 버튼 토글
    const togglePasswordResetForm = () => {
        setIsPasswordResetFormVisible(!isPasswordResetFormVisible);
    };

    // 비밀번호 찾기 버튼 클릭 핸들러
    const handleResetEmail = () => {
        
        // 만약 email 입력하는 창에 아무것도 적지 않고 인증번호 발송 버튼 누르면 백엔드에 요청 안보냄
        if (!findPasswordEmail) {
            message.warning('이메일을 입력해주세요.');
            return;
        }

        // 비밀번호 찾기에서 email창에 입력받은 변수 ex) qkrtlghd97을 백엔드에 보낼 땐 @g.hongik.ac.kr를 달아줘서 보내야함
        const convertedToEmail = findPasswordEmail + fixedEmailDomain;

        // 백엔드에 보낼 email관련 RequestParam 작성
        const emailParams = new URLSearchParams({ email: convertedToEmail });
        console.log('findPasswordEmail', convertedToEmail);

        // 백엔드에 이메일 발송 관련 요청 보냄
        request('POST', `/resetPassword?${emailParams}`)
            .then((response) => {
                if (response.data.sented === true) { // 메일 보내는 데에 성공한 경우
                    message.success('해당 이메일로 새로운 비밀번호가 발송되었습니다.');
                    setResetPasswordMailSented(true); // 메일 전송 상태변수를 true로 세팅
                    
                } else if (response.data.sented === false) { // 메일 보내는 데에 실패한 경우
                    message.error('등록되지 않은 이메일 주소입니다. 먼저 회원가입을 해주세요.');
                }
            })
            .catch((error) => {
                alert("잠시 후 다시 시도하세요.");
            });
    }

    return (
        <Row justify="center">
            <Col span={12}>
                <Tabs activeKey={active} onChange={(key) => setActive(key)} centered>
                    <Tabs tab="Login" key="login">
                        <form onSubmit={onSubmitLogin}>
                            {/** mb-4 : "margin Bottom 4"를 의미하며 요소 하단에 여백을 적용하는 데 사용 */}
                            <div className="form-outline mb-4">
                                <Input
                                    type="text"
                                    name="email"
                                    placeholder="홍익대학교 이메일 계정을 입력해주세요!"
                                    onChange={onChangeHandler}
                                />
                            </div>
                            <div className="form-outline mb-4">
                                <Input
                                    type="password"
                                    name="password"
                                    placeholder="패스워드를 입력해주세요!"
                                    onChange={onChangeHandler}
                                />
                            </div>
                            <Button type="primary" block htmlType="submit">Log In</Button>
                        </form>
                        <br/>
                        <div style={{ textAlign: 'end', cursor: 'pointer' }}>
                            <p onClick={togglePasswordResetForm}>
                                {isPasswordResetFormVisible ? "숨기기" : "비밀번호 찾기"}
                            </p>
                        </div>
                        {/* 비밀번호 찾기를 눌렀을 때, 추가적으로 보이는 창 */}
                        {isPasswordResetFormVisible && (
                            <div>
                                <div className="form-outline mb-4">
                                    <div style={{ display: "flex" }}>
                                        <Input
                                            type="text"
                                            name="findPasswordEmail"
                                            placeholder="비밀번호 재설정을 위한 홍익대학교 이메일 계정을 입력해주세요!"
                                            onChange={onChangeHandler}
                                            disabled={resetPasswordMailSented} // 비밀번호 찾기 버튼을 누른 경우, 이메일 못바꾸게 하기 위함
                                            style={{ flex: 1 }}
                                        />
                                        <Input
                                            type="text"
                                            name="emailDomain"
                                            value="@g.hongik.ac.kr"
                                            style={{ 
                                                    fontWeight: 'strong',
                                                    pointerEvents: 'none', 
                                                    width: '9em', // Adjust the width as needed
                                                    textAlign: 'center' }} // @g.hongik.ac.kr은 고정되게 세팅하되, 회색 배경이 아닌 형태로 만들기 위함
                                        />

                                        <Button onClick={handleResetEmail}>비밀번호 재설정 메일 발송</Button>
                                    </div>
                                    <div style={{ marginTop: '5px', color: "#3399FF" }}>
                                        * 이메일을 잘못 입력했거나 인증코드를 받지 못하셨을 경우 새로고침으로 재시도해주세요 *
                                    </div>
                                </div>
                            </div>
                        )}
                    </Tabs>
                    <Tabs tab="Register" key="register">
                        <form onSubmit={onSubmitRegister}>
                            <div className="form-outline mb-4">
                                <Input
                                    type="text"
                                    name="userName" // 회원 이름
                                    placeholder="실명을 입력해주세요!"
                                    onChange={onChangeHandler}

                                />
                            </div>
                            <div className="form-outline mb-4">
                                <div style={{ display: "flex" }}>
                                    <Input
                                        type="text"
                                        name="nickName" // 회원 닉네임
                                        placeholder="사용하실 닉네임을 입력해주세요!"
                                        onChange={onChangeHandler}
                                    />
                                    <Button onClick={handleDuplicateCheck}>닉네임 중복 확인</Button>
                                </div>
                                {nicknameAvailability !== null && ( // 중복 확인 버튼 눌러서 중복 확인 여부를 알아왔을 때,
                                    // 사용 가능한 닉네임인 경우 초록색으로 아래에 사용 가능하단 문구를 렌더링
                                    // 사용 불가능한 닉네임인 경우 빨간색으로 아래에 사용 불가능하단 문구를 렌더링
                                    // 빈 문자열로 중복확인 한 경우 빨간색으로 다시 입력하라는 문구를 렌더링
                                    <div className={nicknameAvailability ? "verification-success" : "verification-failure"} style={{ color: (nickName === "" || !nicknameAvailability) ? "#ff4d4f" : "#00cc00" }}>
                                        {(() => {
                                            if (nickName === "") {
                                                return "빈 문자열로는 닉네임을 생성할 수 없습니다. 다시 입력하세요"
                                            }
                                            else if (!nicknameAvailability) {
                                                return "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력하세요."
                                            }
                                            else {
                                                return "사용 가능한 닉네임입니다!"
                                            }
                                        })()}
                                    </div>
                                )}
                            </div>
                            <div className="form-outline mb-4">
                                <div style={{ display: "flex" }}>
                                    <Input
                                        type="text"
                                        name="email"
                                        placeholder="홍익대학교 이메일 계정을 입력해주세요!"
                                        onChange={onChangeHandler}
                                        disabled={(verificationSuccess === 1) || mailSented} // 인증 번호 발송이 된 경우 || 인증이 완료된 경우 이메일 못바꾸게 하기 위함
                                        style={{ flex: 1 }}
                                    />
                                    <Input
                                        type="text"
                                        name="emailDomain"
                                        value="@g.hongik.ac.kr"
                                        style={{ 
                                                fontWeight: 'strong',
                                                pointerEvents: 'none', 
                                                width: '9em', // Adjust the width as needed
                                                textAlign: 'center' }} // @g.hongik.ac.kr은 고정되게 세팅하되, 회색 배경이 아닌 형태로 만들기 위함
                                    />

                                    <Button onClick={handleSendVerificationCode}>인증코드 발송</Button>
                                </div>
                                <div style={{ marginTop: '5px', color: "#3399FF" }}>
                                    * 이메일을 잘못 입력했거나 인증코드를 받지 못하셨을 경우 새로고침으로 재시도해주세요 *
                                </div>
                            </div>
                            <div className="form-outline mb-4">
                                <div style={{ display: "flex" }}>
                                    <Input disabled={verificationSuccess === 1} // 확인 버튼 눌렀을 때 이메일 인증번호가 맞으면 폼 자체가 disable됨
                                        type="text"
                                        name="userInputCode"
                                        placeholder="이메일 계정으로 발송된 인증 코드를 10분 내에 입력해주세요!"
                                        onChange={onChangeHandler}
                                    />
                                    <Button onClick={handleVerifyCode} disabled={!mailSented || (verificationSuccess === 1)}>확인</Button> {/* 메일 인증을 하지 않은 상태 || 인증 완료된 상태일 때 확인 버튼은 비활성화됨*/}
                                </div>
                                <div className={verificationSuccess === 1 ? "verification-success" : "verification-failure"} style={{ color: verificationSuccess === 1 ? "#00cc00" : "#ff4d4f" }}>
                                    {verificationSuccess !== null && (
                                        verificationSuccess === 1
                                            ? "인증이 완료되었습니다."
                                            : verificationSuccess === 2
                                                ? "인증번호가 올바르지 않습니다. 다시 입력해 주세요."
                                                : verificationSuccess === 0
                                                    ? "인증시간이 만료되었습니다. 인증코드 전송 버튼을 다시 눌러주세요"
                                                    : null
                                    )}
                                </div>
                            </div>

                            <div className="form-outline mb-4">
                                <Input
                                    type="password"
                                    name="password"
                                    placeholder="사용하실 비밀번호를 입력해주세요!"
                                    onChange={onChangeHandler}
                                />
                            </div>
                            <Button type="primary" block htmlType="submit">Sign Up</Button>
                        </form>
                    </Tabs>
                </Tabs>
            </Col>
        </Row>
    );
}

export default LoginPage;