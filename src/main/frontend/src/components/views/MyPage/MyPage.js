// 로그인된 회원만 볼 수 있는 페이지
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Button, Menu, message, Form, Input, Modal, Image } from 'antd';
import { setSaveRecommendedList, setIsRecommededPortfolioView } from "../../../hoc/request";
import { saveRecommendedList, setRecommendPortfolioView } from "../../../_actions/actions";
import { request } from '../../../hoc/request';
import { useDispatch } from 'react-redux';
import { logout } from '../../../_actions/actions'
import { setAuthHeader, setUserRole } from '../../../hoc/request';

import axios from 'axios';
import { getAuthToken } from '../../../hoc/request';


const { Item } = Form;
function MyPage() {

    const navigate = useNavigate();
    const dispatch = useDispatch();

    //드롭다운 관련
    const [selectedOption, setSelectedOption] = useState('info'); //드롭다운(정보수정, 비번 변경, 탈퇴)에서 해당 배너를 클릭할떄마다 selectOption값은 바뀜

    //회원 정보 수정 관련
    const [isUpdateButtonEnabled, setIsUpdateButtonEnabled] = useState(false); // 정보 수정 시, 모든 필드가 입력되어야만 버튼이 활성화됨
    const [userBaseInfo, setUserBaseInfo] = useState(null); // 회원의 email, 닉네임, 이름, 포폴 유무 정보를 받아옴.
    const [nicknameAvailability, setNicknameAvailability] = useState(""); // 닉네임 중복 여부
    const [isConfirm, setIsConfirm] = useState(true);  // 닉네임 중복 확인 눌러서 중복 없으면 true, 중복 있으면 false. 닉네임 칸에 문자열을 변경하는 순간 false로 바뀜.
    // 회원 정보 업데이트랑 기존 정보 받아올 때 둘 다 사용, 업데이트 할 때는 에 비밀번호까지 실어서 보내고, 
    // 다시 useEffect로 GET- /userInfo할때는 userDto로 받음(비밀번호 필드 누락된 dto만 받음)

    //비밀 번호 변경 관련
    const [currentPassword, setCurrentPassword] = useState(''); //기존 비밀번호를 입력하는 필드
    const [newPassword, setNewPassword] = useState(''); //바꾸려는 비밀번호를 입력하는 필드
    const [confirmNewPassword, setConfirmNewPassword] = useState(''); //바꾸려는 비밀번호를 확인하는 필드
    const [isPasswordUpdateButtonEnabled, setIsPasswordUpdateButtonEnabled] = useState(false); //앞선 세 가지 필드가 다 입력되어야 비밀번호 변경 버튼이 활성화됨

    //회원 탈퇴 관련
    const [currentPasswordForSignOut, setCurrentPasswordForSignOut] = useState(''); //기존 비밀번호를 입력하는 필드
    const [isSignOutButtonEnabled, setIsSignOutButtonEnabled] = useState(false); //기존 비밀번호를 입력하는 필드가 입력되어야 탈퇴하기 버튼이 활성화됨
    const [isWithdrawModalVisible, setIsWithdrawModalVisible] = useState(false); // 정말로 삭제하시겠습니까? 라는 내용을 보여주는 모달 창을 보여줄지 말지 여부 설정


    const [selectedImage, setSelectedImage] = useState(null); //업로드할 이미지, 내 도큐먼트에서 선택한거
    const [previewImage, setPreviewImage] = useState(null); // To store the image to be previewed
    const [previewVisible, setPreviewVisible] = useState(false); // To control the visibility of the preview modal
    const [profileImage, setProfileImage] = useState(null); //이미 등록되어있는 프사 띄우는 용도
    const [profileUploaded, setProfileUploaded] = useState(false);
    const [remove, setRemove] = useState(null);


    // MyPage가 마운트 될 때 /userInfo에서 데이터를 가져와 data에 세팅 -> userDto값이 세팅되어짐
    useEffect(() => {
        request('GET', '/userInfo', {})
            .then((response) => {
                setUserBaseInfo(response.data);
                dispatch(setRecommendPortfolioView(false));
                dispatch(saveRecommendedList(null));
                setIsRecommededPortfolioView(false);
                setSaveRecommendedList(null);
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
        
    }, []);

    // 회원 정보 수정 관련 - 버튼 활성화를 컨트롤하는 장치
    useEffect(() => {
        // 세 개의 입력 칸(닉네임, 패스워드, 이름)이 모두 입력되면 정보 수정하기 버튼 클릭 가능
        const isRequiredFieldsFilled = userBaseInfo && userBaseInfo.nickName && userBaseInfo.userName && userBaseInfo.password;
        setIsUpdateButtonEnabled(isRequiredFieldsFilled); //만약 하나라도 입력되지 않으면 버튼 활성화되지 않음
    }, [userBaseInfo]);

    // 비밀 번호 변경 관련 - 버튼 활성화를 컨트롤하는 장치
    useEffect(() => {
        // 세 개의 입력 칸(기존 비밀번호, 바꾸려는 비밀번호, 바꾸려는 비밀번호 확인)이 모두 입력되면 비밀번호 변경 버튼 클릭 가능
        const changePasswordFieldFilled = currentPassword && newPassword && confirmNewPassword;
        setIsPasswordUpdateButtonEnabled(changePasswordFieldFilled); //만약 하나라도 입력되지 않으면 버튼 활성화되지 않음

    }, [currentPassword, newPassword, confirmNewPassword]);

    // 회원 탈퇴 관련 - 버튼 활성화를 컨트롤하는 장치
    useEffect(() => {
        // 한 개의 입력 칸(기존 패스워드)가 입력되면 회원 탈퇴하기 버튼 클릭 가능
        const signOutPasswordFieldFilled = currentPasswordForSignOut;
        setIsSignOutButtonEnabled(signOutPasswordFieldFilled); //만약 하나라도 입력되지 않으면 버튼 활성화되지 않음
    }, [currentPasswordForSignOut])


    //프로필 사진 백에서 가져오기
    useEffect(()=>{

        request('GET', '/userProfileImage')
            .then((response) => {
                console.log(response.data.imageUrl);
                setProfileImage(response.data.imageUrl);
                setProfileUploaded(false);
            })
            .catch((error) => {
                console.error("Error fetching profile image:", error);
            });

    }, [profileImage])



    //드롭다운에서 특정 배너를 클릭하면 변경되는 기능
    const handleMenuClick = (e) => {
        setSelectedOption(e.key);
    };
    const handlePreview = (image) => {
        setPreviewImage(image);
        setPreviewVisible(true);
    };

    //회원 정보 수정 드롭다운 내에서, 필드 값의 변경을 감지하고 세팅하는 장치 - (filedName : nickName, userName, password) / (value : 변경하려는 값)
    const handleInputChange = (fieldName, value) => {
        // prevData로 이전의 회원 정보 변경 관련하여 입력된 필드 상태 값을 가져오고, value를 사용하여 이름이 fieldName인 속성을 추가하거나 업데이트하여 새 상태 값을 반환
        setUserBaseInfo((prevData) => ({ ...prevData, [fieldName]: value }));

        if (fieldName === 'nickName') {
            setIsConfirm(false);    // 닉네임이 변경되므로, isConfirm을 false로 바꿔주기
        }
    };

    const handleRemoveSelectedImage = () => {
        setSelectedImage(null);
        console.log("selectedImage" , selectedImage);
        console.log("remove" , remove);
        
    };

    const handleResetProfileImage = () =>{
        setRemove(true);
        console.log("selectedImage" , selectedImage);
        console.log("remove" , remove);
    };

    const handleRemove = () =>{
        console.log("haneldRemove🩸");
        selectedImage ? handleRemoveSelectedImage() : handleResetProfileImage();
    };

    const handleSet = () => {
        const fileInput = document.getElementById("fileInput");
        if (fileInput) {
          fileInput.value = ''; // Reset the file input
        }
        fileInput.click(); // Trigger a click event on the file input
      };


    //수정하기 버튼 누면 일어나는 액션
    //백으로 닉네임, 이름, 비밀번호, 프사 전달
    const updateInfo = (updatedData) => {
        if (updatedData.nickName && updatedData.userName && updatedData.password) { //기본정보 필수로 다 입력해야 작동
            // 닉네임 중복 확인을 한 경우에만 request
            if (isConfirm) {
                request('PUT', '/updateUserInfo', updatedData)
                    .then((response) => {
                        if (response.data === "User information has been successfully updated.") {
                            alert('정보가 업데이트되었습니다.');
                            setUserBaseInfo((prevData) => ({ ...prevData, ...updatedData, password: '' }));

                            //기본정보가 백으로 전달되고 나면
                            //프로필 사진 업데이트 시작
                            if(remove){
                                return removeProfileImage();
                                

                            }else{return handleSubmit(); }
                            
                        } else {
                            console.error('Unknown response:', response.data);
                            message.error('정보 업데이트에 실패했습니다.');
                        }
                    })
                    .then((secondResponse) => {
                        if (secondResponse === 'success') { //프사 업데이트(secondResponse)가 성공하면
                        } else {
                        }
                    })
                    .catch((error) => {
                        console.error('Error updating information:', error);
                        message.error('정보 업데이트 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
                    });
            } else {
                message.warning('닉네임 중복 확인을 눌러주세요!');
            }
        } else {
            //모든 필수정보 다 입력하지 않으면
            message.warning('모든 필수 정보를 입력하세요.');
        }
    };
    
    const removeProfileImage = () =>{
        
        return new Promise((resolve, reject) => {
                
                const formData = new FormData();
                formData.append('imageUrl', selectedImage);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': `Bearer ${getAuthToken()}`,
                    },
                };
                
                axios
                    .put(`/removeProfileImage`, formData, config)
                    .then((response) => {
                        if (response.data === 'success') {
                            setProfileUploaded(true);
                            setProfileImage(null);
                            setRemove(false);
                            window.location.reload();
                            resolve('success'); 
                        } else {
                            console.error('Unknown response:', response.data);
                            message.error('Failed to update information.');
                            reject('failure'); 
                        }
                    })
                    .catch((error) => {
                        reject('failure'); 
                    });
       
        });

    }
    //프사 업데이트 함수, 따로 매개변수는 없고 useState를 static하게 바로 사용
    const handleSubmit = () => {
        return new Promise((resolve, reject) => {
            if (selectedImage) {
                const formData = new FormData();
                formData.append('imageUrl', selectedImage);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': `Bearer ${getAuthToken()}`,
                    },
                };
                
                axios
                    .post('/updateProfileImage', formData, config)
                    .then((response) => {
                        if (response.data === 'success') {
                            setSelectedImage(null);
                            setProfileUploaded(true);
                            window.location.reload();
                            resolve('success'); 
                        } else {
                            console.error('Unknown response:', response.data);
                            message.error('Failed to update information.');
                            reject('failure'); 
                        }
                    })
                    .catch((error) => {
                        reject('failure'); 
                    });
            } else {
                resolve('noImage'); 
            }
        });
    };
    
    
    
    

    // '회원 정보 변경'과 관련하여 백엔드에 request를 보내고, 그에 대한 response 처리를 하는 곳
    

    // '비밀 번호 변경'과 관련하여 백엔드에 request를 보내고, 그에 대한 response 처리를 하는 곳
    const updatePassword = () => {
        if (newPassword === confirmNewPassword) { //새로운 비밀번호와 새로운 비밀번호 확인 값이 같을 경우에만 백엔드 요청 보냄

            const passwordData = { //백엔드에 보낼 dto객체 생성 ->  UserPasswordUpdateDto(백엔드)

                currentPassword: currentPassword,
                password: newPassword,
                confirmNewPassword: confirmNewPassword
            };

            request('PUT', '/updatePassword', passwordData)
                .then((response) => {
                    if (response.data === "Password updated successfully.") {

                        message.success('비밀번호가 성공적으로 업데이트되었습니다.');
                        setCurrentPassword('')
                        setNewPassword('');
                        setConfirmNewPassword('');
                    } else {

                        console.error('Unknown response:', response.data);
                        message.error('비밀번호 업데이트에 실패했습니다.');
                    }
                })
                .catch((error) => { //백엔드에서 예외를 보냈을 경우

                    if (error.response && error.response.data) { //예외 데이터를 파싱(문자열을 확인)
                        const errorMessage = error.response.data;

                        if (errorMessage === "Current password is incorrect") { //기존의 비밀번호가 틀렸을 경우
                            message.warning('기존 비밀번호를 정확히 입력하세요. 다시 시도하세요.');
                        } else {
                            message.error('비밀번호 업데이트 중 오류가 발생했습니다.');
                        }
                    } else {
                        console.error('Error updating password:', error);
                        message.error('비밀번호 업데이트 중 오류가 발생했습니다.');
                    }
                });
        } else {
            //비밀번호와 비밀번호 확인 필드가 일치하지 않을 경우 백엔드에 따로 요청 안보내고 프런트 내에서 에러메세지 반환
            message.warning('새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.');
        }
    };

    // '회원 탈퇴'와 관련하여 백엔드에 request를 보내고, 그에 대한 response 처리를 하는 곳
    const onSignOutHandler = () => {

        const signOutPasswordData = { //백엔드에 보낼 dto객체 생성 ->  signOuDto(백엔드)
            currentPasswordForSignOut: currentPasswordForSignOut
        };

        request('POST', '/signOut', signOutPasswordData)
            .then((response) => {
                if (response.data === "User has been successfully withdrawn.") {
                    message.success('회원 탈퇴가 완료되었습니다.');
                    setAuthHeader(null); //탈퇴하였으므로 해당 회원의 로컬스토리지에 있는 토큰을 비우기
                    setUserRole(null); //탈퇴하였으므로 해당 회원의 로컬스토리지에 있는 role을 비우기
                    localStorage.clear();  //로컬스토리지 클리어 반드시 해주기!! 얘 안하면 로그아웃 상태에서 새로고침 시 랜딩페이지가 렌더링되지 않음!!
                    dispatch(logout()); //로그 아웃 상태로 전환
                    navigate('/');
                }
                else {

                    console.error('Unknown response:', response.data);
                    message.error('회원 탈퇴에 실패했습니다.');
                }
            })
            .catch((error) => {
                if (error.response && error.response.data) {
                    const errorMessage = error.response.data;

                    if (errorMessage === "Current password is incorrect") { //기존의 비밀번호가 틀렸을 경우
                        message.warning('기존 비밀번호를 정확히 입력하세요. 다시 시도하세요.');
                    } else {
                        message.warning('회원 탈퇴에 실패했습니다.');
                    }
                } else {
                    console.error("Error signing out:", error);
                    message.warning('회원 탈퇴에 실패했습니다.');
                }
            });
    }

    // 확인 버튼을 누른 경우
    const handleWithdrawConfirm = () => {
        setIsWithdrawModalVisible(false); // Close the modal
        onSignOutHandler(); // Call the function to perform account withdrawal
    };
    
    // 취소 버튼을 누른 경우
    const handleWithdrawCancel = () => {
        setIsWithdrawModalVisible(false); // Close the modal
    };
    
    // 닉네임 중복 체크
    const handleDuplicateCheck = () => {
        request('GET', `/nicknameDuplicateString?nickname=${userBaseInfo.nickName}`) //백엔드에 현재 입력받은 nickname을 가진 회원이 있는 지 찾고, 백엔드는 해당 닉네임으로 유저 생성 가능하면 available:true /불가능하면 available:false 반환
            .then((response) => {
                const isAvailable = response.data.available;
                setNicknameAvailability(isAvailable); //닉네임 사용 가능 여부 값을 상태변수에 저장
                setIsConfirm(true); // 중복 확인 클릭 true
            })
            .catch((error) => {
                setIsConfirm(false); // 중복 확인 클릭 false
                alert("잠시 후 다시 시도해보세요.");
            });
    };

    return (
        <div style={{width:'1200px'}}>
            <div style={{ marginTop:'10px', display: 'flex', flexDirection: 'row' }}>
                <div style={{ width: '18%' }}>
                    <Menu mode="vertical" selectedKeys={[selectedOption]} onClick={handleMenuClick}>
                        <Menu.Item key="info">정보 수정</Menu.Item>
                        <Menu.Item key="password">비밀번호 변경</Menu.Item>
                        <Menu.Item key="withdrawal">회원 탈퇴</Menu.Item>
                    </Menu>
                </div>
                <div style={{ width: '75%' }}>
                    {selectedOption === 'info' && (
                        <Card title="정보 수정" style={{ width: '100%' }}>
                            {userBaseInfo && (
                                <Form>
                                    <div style={{display:'flex', paddingLeft:'20px', paddingRight:'20px',alignItems: 'center' }}>
                                        <div style={{display:'grid', width :'1200px'}}>
                                            <div style={{marginTop:'20px'}}>
                                                <div style={{display:'flex', marginRight:'10px', marginBottom:'10px', }}>
                                                    <div style={{marginRight:'10px', width:'90px'}}>
                                                        이메일
                                                    </div>
                                                    <div>
                                                        <Input
                                                                type="email"
                                                                value={userBaseInfo.email}
                                                                readOnly
                                                                disabled // Prevent interaction with the field
                                                                style={{ backgroundColor: '#f0f0f0', width:'400px' }} />

                                                    </div>
                                                </div>
                                                <div style={{display:'flex', marginRight:'10px', marginBottom:'10px'}}>
                                                    <div style={{marginRight:'10px', width:'90px'}}>
                                                        닉네임
                                                    </div>
                                                    <div>
                                                        <Input
                                                                type="text"
                                                                value={userBaseInfo.nickName}
                                                                placeholder = "닉네임을 입력해주세요"
                                                                onChange={(e) => handleInputChange('nickName', e.target.value)}
                                                                style={{ width:'300px' }}
                                                            />
                                                    </div>
                                                    <div>
                                                        <Button onClick={handleDuplicateCheck} style={{ marginLeft: '10px' , width : "90px"}}>중복 확인</Button>
                                                    </div>
                                                </div>
                                                <div style={{display:'flex', marginRight:'10px', marginBottom:'10px'}} >
                                                    {nicknameAvailability !== "" && ( // 중복 확인 버튼 눌러서 중복 확인 여부를 알아왔을 때,
                                                        // 사용 가능한 닉네임인 경우 초록색으로 아래에 사용 가능하단 문구를 렌더링
                                                        // 사용 불가능한 닉네임인 경우 빨간색으로 아래에 사용 불가능하단 문구를 렌더링
                                                        // 빈 문자열로 중복확인 한 경우 빨간색으로 다시 입력하라는 문구를 렌더링
                                                        <div className={nicknameAvailability ? "verification-success" : "verification-failure"} style={{ fontSize: '12px', marginLeft: '100px' ,marginBottom: '20px', marginTop: '', color: (userBaseInfo.nickName === "" || !nicknameAvailability) ? "#ff4d4f" : "#00cc00"}}>
                                                            {(() => {
                                                                if (userBaseInfo.nickName === "") {
                                                                    return "빈 문자열로는 닉네임을 생성할 수 없습니다. 다시 입력하세요"
                                                                }
                                                                else if (nicknameAvailability === "false") {
                                                                    return "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력하세요."
                                                                }
                                                                else if (nicknameAvailability === "me") {
                                                                    return "본인이 현재 사용중인 닉네임입니다."
                                                                }
                                                                else if (userBaseInfo.nickName.length > 10) {
                                                                    return "닉네임은 최대 10자까지 입력 가능합니다."
                                                                }
                                                                // else if (userBaseInfo.nickName === )
                                                                else {
                                                                    return "사용 가능한 닉네임입니다!"
                                                                }
                                                            })()}
                                                        </div>
                                                    )}
                                                </div>
                                                <div style={{display:'flex', marginRight:'10px', marginBottom:'10px'}}>
                                                    <div style={{marginRight:'10px', width:'90px'}}>
                                                        성명
                                                    </div>
                                                    <div>
                                                        <Input
                                                                type="text"
                                                                value={userBaseInfo.userName}
                                                                placeholder="이름을 입력해주세요"
                                                                onChange={(e) => handleInputChange('userName', e.target.value)}
                                                                style={{ width:'400px' }}  />
                                                    </div>
                                                </div>
                                                <div style={{display:'flex', marginRight:'10px', marginBottom:'10px'}}>
                                                    <div style={{marginRight:'10px', width:'90px'}}>
                                                        비밀번호
                                                    </div>
                                                    <div>
                                                        <Input
                                                                    type="password"
                                                                    value={userBaseInfo.password || ''} //비밀번호는 백엔드에서 가져오지 못했으므로 빈칸으로 세팅
                                                                    placeholder="비밀번호를 입력해주세요"
                                                                    onChange={(e) => handleInputChange('password', e.target.value)}
                                                                    style={{ width:'400px' }}  />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div style={{marginRight:'30px'}}>
                                            {/* 이미 있는 프사 있으면 띄움 */}
                                            {/* 로컬에서 선택한 이미지가 있으면 그걸 띄우고 기존 프사는 띄우지 않음 */}
                                            <div style={{ display: 'flex', marginBottom: '8px' }}>
                                                {(remove) ? (
                                                    <Image
                                                    style={{ borderRadius: '50%', width: '190px', height: '190px', marginBottom: '15px', border: '5px solid salmon', zIndex: 1 }}
                                                    src={`https://storage.googleapis.com/hongik-pickme-bucket/comgongWow.png`}
                                                />

                                                ) : (null)}

                                                {!remove && selectedImage ? (
                                                    //새로 바꿀 이미지
                                                    <Image
                                                    src={URL.createObjectURL(selectedImage)}
                                                    style={{ borderRadius: '50%', width: '200px', height: '200px', marginBottom: '15px', border: '5px solid salmon', zIndex: 0 }}
                                                    onClick={() => handlePreview(URL.createObjectURL(selectedImage))} // Open the modal when clicked
                                                    />
                                                ):(
                                                    //기존 프사
                                                    null

                                                )}
                                                {!remove && !selectedImage ? (
                                                    <Image
                                                    style={{ borderRadius: '50%', width: '190px', height: '190px', marginBottom: '15px', border: '5px solid salmon', zIndex: 0 }}
                                                    src={`https://storage.googleapis.com/hongik-pickme-bucket/${profileImage}`}
                                                />
                                                ):(null)}
                                                    
                                                    
                                            </div>
                                            <div style={{ display: 'flex', justifyContent: 'center' }}>
                                                {/* 업로드할 사진 */}
                                                    <input
                                                    type="file"
                                                    id="fileInput"
                                                    accept="image/*"
                                                    style={{ display: 'none' }}
                                                    onChange={(event) => {
                                                        setSelectedImage(event.target.files[0]);
                                                        console.log("selected! " , selectedImage);
                                                        // Handle the selected image as needed
                                                        setRemove(false);
                                                    }}
                                                />
                                                <span style={{cursor: 'pointer'}}
                                                    onMouseUp={handleSet}
                                                >
                                                    ⚙️ set image
                                                </span>
                                                <span 
                                                    style={{marginLeft:'30px', cursor:'pointer'}}
                                                    onMouseUp={()=>handleRemove()}
                                                >
                                                    remove
                                                </span>

                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div style={{marginTop:'10px'}}>
                                        <Button type="primary" onClick={() => updateInfo(userBaseInfo)}
                                            disabled={!isUpdateButtonEnabled}>
                                            정보 업데이트
                                        </Button>

                                    </div>

                                    
                                </Form>
                            )}
                        </Card>
                    )}
                    {selectedOption === 'password' && (
                        <Card title="비밀번호 변경" style={{ width: '100%' }}>
                            <Form>
                                <div style={{marginTop:'15px', display:'grid', width :'1200px', paddingLeft:'20px', paddingRight:'20px'}}>
                                    <div style={{display:'flex', marginRight:'10px', marginBottom:'10px', }}>
                                        <div style={{marginRight:'10px', width:'90px'}}>
                                            이메일
                                        </div>
                                        <div>
                                            <Input
                                                type="email"
                                                value={userBaseInfo.email} //이메일은 화면에 보여주되, 변경 불가능하게 disable설정
                                                readOnly
                                                disabled
                                                style={{ backgroundColor: '#f0f0f0', width:'400px' }}
                                            />
                                        </div>
                                    </div>
                                    <div style={{display:'flex', marginRight:'10px', marginBottom:'10px', }}>
                                        <div style={{marginRight:'10px', width:'90px'}}>
                                            기존 비밀번호
                                        </div>
                                        <div>
                                            <Input
                                                    type="password"
                                                    value={currentPassword}
                                                    placeholder="기존에 사용하던 비밀번호를 입력해주세요"
                                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                                    style={{  width:'400px' }}
                                                />
                                        </div>
                                    </div>
                                    <div style={{display:'flex', marginRight:'10px', marginBottom:'10px', }}>
                                        <div style={{marginRight:'10px', width:'90px'}}>
                                            새로운 비밀번호
                                        </div>
                                        <div>
                                            <Input
                                                type="password"
                                                value={newPassword}
                                                placeholder = "기존 비밀번호와 다른 비밀번호를 입력해주세요"
                                                onChange={(e) => setNewPassword(e.target.value)}
                                                style={{  width:'400px' }}
                                            />
                                        </div>
                                    </div>
                                    <div style={{display:'flex', marginRight:'10px', marginBottom:'10px', }}>
                                        <div style={{marginRight:'10px', width:'90px'}}>
                                            비밀번호 확인
                                        </div>
                                        <div>
                                            <Input
                                                type="password"
                                                value={confirmNewPassword}
                                                placeholder = "새로운 비밀번호를 다시 입력해주세요"
                                                onChange={(e) => setConfirmNewPassword(e.target.value)}
                                                style={{  width:'400px' }}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <Button
                                    type="primary"
                                    onClick={updatePassword}
                                    disabled={!isPasswordUpdateButtonEnabled}
                                >
                                    비밀번호 변경
                                </Button>
                            </Form>
                        </Card>
                    )}
                    {selectedOption === 'withdrawal' && (
                        <Card title="회원 탈퇴" style={{ width: '100%' }}>
                            <Item label="기존 비밀번호">
                                <Input
                                    type="password"
                                    value={currentPasswordForSignOut}
                                    onChange={(e) => setCurrentPasswordForSignOut(e.target.value)}
                                />
                            </Item>
                            {/* 탈퇴 버튼 */}
                            <Button
                                type="primary"
                                onClick={() => setIsWithdrawModalVisible(true)}
                                disabled={!isSignOutButtonEnabled}>
                                탈퇴하기
                            </Button>
                            {/** Ok와 Cancel 함수가 크로스 되어 있음 */}
                            <Modal
                                title="회원 탈퇴 확인"
                                open={isWithdrawModalVisible}
                                onOk={handleWithdrawConfirm}
                                onCancel={handleWithdrawCancel}
                                okText="예"
                                cancelText="아니오"
                            >
                                <p>정말로 탈퇴하시겠습니까?</p>
                            </Modal>
                        </Card>
                    )}
                </div>
            </div>
        </div>
    );

}

export default MyPage;