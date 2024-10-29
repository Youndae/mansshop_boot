import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";

function AdminReviewDetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { reviewId } = useParams();

    const [data, setData] = useState({
        productName: '',
        productOption: '',
        writer: '',
        createdAt: '',
        updatedAt: '',
        content: '',
        replyUpdatedAt: '',
        replyContent: '',
    });
    const [inputValue, setInputValue] = useState('');

    const dispatch = useDispatch();

    useEffect(() => {
        getReviewDetail(reviewId);
    }, [reviewId]);

    const getReviewDetail = async (reviewId) => {
        await axiosInstance.get(`admin/review/detail/${reviewId}`)
            .then(res => {
                const responseContent = res.data.content;
                const optionSize = `사이즈 : ${responseContent.size}`;
                const optionColor = `컬러 : ${responseContent.color}`;
                let option = '';
                if(responseContent.size == null){
                    if(responseContent.color != null)
                        option = optionColor;
                }else {
                    if(responseContent.color == null)
                        option = optionSize;
                    else
                        option = `${optionSize}, ${optionColor}`;
                }

                setData({
                    productName: responseContent.productName,
                    productOption: option,
                    writer: responseContent.writer,
                    createdAt: responseContent.createdAt,
                    updatedAt: responseContent.updatedAt,
                    content: responseContent.content,
                    replyUpdatedAt: responseContent.replyUpdatedAt,
                    replyContent: responseContent.replyContent
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    const handleInputSubmit = async () => {
        if(inputValue === ''){
            alert('답글 내용을 입력해주세요');
        }else {
            await axiosInstance.post(`admin/review`, {
                reviewId: reviewId,
                content: inputValue,
            })
                .then(res => {
                    if(checkResponseMessageOk(res)) {
                        setInputValue('');
                        getReviewDetail(reviewId);
                    }
                })
        }
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'review'}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>상품 리뷰</h1>
                </div>
                <div className="mypage-qna-content">
                    <div className="mypage-qna-content-title">
                        <h2>{data.productName}({data.productOption})</h2>
                        <small className="qna-reply-date">작성일 : {data.createdAt}, 수정일 : {data.updatedAt}</small>
                    </div>
                    <div className="mypage-qna-content-content">
                        <p className="qna-detail-content">
                            {data.content}
                        </p>
                    </div>
                    <div className="qna-reply-input">
                        <textarea className="reply-input-textarea" onChange={handleInputOnChange} value={inputValue}>{inputValue}</textarea>
                        <DefaultBtn onClick={handleInputSubmit} btnText={'작성'}/>
                    </div>
                    <div className="mypage-qna-content-reply">
                        <AdminReviewReply data={data}/>
                    </div>
                </div>
            </div>
        </div>
    )
}

function AdminReviewReply(props) {
    const { data } = props;

    if(data.replyContent === null)
        return null;
    else{
        return (
            <div className="qna-detail-reply">
                <div className="qna-reply-writer">
                    <strong>관리자</strong>
                    <small className="qna-reply-date">{data.replyUpdatedAt}</small>
                </div>
                <div className="qna-reply-content">
                    <p>{data.replyContent}</p>
                </div>
            </div>
        )
    }
}

export default AdminReviewDetail;