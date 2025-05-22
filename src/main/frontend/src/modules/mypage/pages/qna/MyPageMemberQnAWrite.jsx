import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";

import {
	getQnAClassificationList,
	postMemberQnA
} from '../../services/mypageQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import MyPageSideNav from '../../components/MyPageSideNav';
import MemberQnAWriteForm from '../../components/MyPageMemberQnAWriteForm';

//회원 문의 작성 페이지
function MyPageMemberQnAWrite() {
	const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');

    const navigate = useNavigate();

	useEffect(() => {
		const getClassification = async() => {
			try{
				const res = await getQnAClassificationList();

				setClassification(res.data.classificationList);
                setClassificationId(res.data.classificationList[0].id);
			} catch(err) {
				console.log(err);
			}
		}

		getClassification();
	}, []);
	
	// 작성 이벤트
	const handleSubmit = async() => {
		try {
			const res = await postMemberQnA(inputData, classificationId);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				navigate(`/my-page/qna/member/detail/${res.data.memberQnAId}`);
		} catch(err) {
			console.log(err);
		}
	}

	//문의 분류 select box 이벤트
	const handleSelectOnChange = (e) => {
		setClassificationId(e.target.value);
	}

	//제목 및 textarea 입력 이벤트
	const handleInputOnChange = (e) => {
		setInputData({
			...inputData,
			[e.target.name]: e.target.value
		});
	}
	
	return (
		<div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의하기</h1>
                </div>
                <MemberQnAWriteForm
                    inputData={inputData}
                    classificationId={classificationId}
                    classification={classification}
                    handleInputOnChange={handleInputOnChange}
                    handleSelectOnChange={handleSelectOnChange}
                    handleSubmit={handleSubmit}
                    btnText={'작성'}
                />
            </div>
        </div>
	)
	
}

export default MyPageMemberQnAWrite;