import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { 
	getProductQnADetail, 
	deleteProductQnA } from '../../services/mypageQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import MyPageSideNav from '../../components/MyPageSideNav';
import QnADetail from '../../../../common/components/QnADetail';

/*
    상품 문의 상세 페이지
    사용자는 답변 작성 불가.

    단순히 작성한 문의 내용을 보여주고 삭제만 가능.
 */
function MyPageProductQnADetail() {
	const { qnaId } = useParams();

	const [data, setData] = useState({
        productQnAId: ''
        , title: ''
        , writer: ''
        , qnaContent: ''
        , date: ''
        , qnaStatus: ''
    });
    const [replyData, setReplyData] = useState([]);

    const navigate = useNavigate();

	useEffect(() => {
		const getProductQnA = async () => {
			try {
				const res = await getProductQnADetail(qnaId);

				setData({
                    productQnAId: res.data.productQnAId
                    , title: `상품명 : ${res.data.productName}`
                    , writer: res.data.writer
                    , qnaContent: res.data.qnaContent
                    , date: res.data.createdAt
                    , qnaStatus: res.data.productQnAStat
                })

                let replyArr = [];
                const replyList = res.data.replyList;

                for(let i = 0; i < replyList.length; i++) {
                    replyArr.push({
                        replyId: replyList[i].replyId,
                        writer: replyList[i].writer,
                        replyContent: replyList[i].replyContent,
                        updatedAt: replyList[i].updatedAt,
                        inputStatus: false,
                    });
                }

                setReplyData(replyArr);

			} catch(err) {
				console.log(err);
			}
		}

		getProductQnA();
	}, [qnaId]);

	//문의 삭제 버튼 이벤트
	const handleDeleteBtn = async() => {
		try {
			const res = await deleteProductQnA(qnaId);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				navigate('/my-page/qna/product');
			
		} catch(err) {
			console.log(err);
		}
	}

	return (
		<div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <QnADetail
                data={data}
                replyData={replyData}
                handleReplyModifyElement={null}
                handleModifyOnChange={null}
                modifyTextValue={null}
                handleModifySubmit={null}
                handleInputOnChange={null}
                inputValue={null}
                handleInputSubmit={null}
                titleText={'상품 문의'}
                handleDeleteBtn={handleDeleteBtn}
                replyStatus={false}
            />
       </div>			
	)
}

export default MyPageProductQnADetail;