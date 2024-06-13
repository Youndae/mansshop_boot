import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import QnADetail from "./QnADetail";

function MyPageProductQnADetail() {
    const userState = useSelector((state) => state.member);
    const loginStatus = userState.loginStatus;
    const nickname = userState.id;
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
    const [modifyTextValue, setModifyTextValue] = useState('');
    const [inputValue, setInputValue] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getProductQnADetail();
    }, [qnaId]);

    const getProductQnADetail = async () => {

        await axiosInstance.get(`my-page/qna/product/detail/${qnaId}`)
            .then(res => {
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

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('productQnA error : ', err);
            })
    }

    const handleReplyModifyOpen = (e) => {
        setReplyModifyStatus(e, true);

    }

    const handleReplyModifyClose = (e) => {
        setReplyModifyStatus(e, false);
    }

    const setReplyModifyStatus = (e, status) => {
        const idx = e.target.value;

        replyData[idx] = {
            replyId: replyData[idx].replyId,
            writer: replyData[idx].writer,
            replyContent: replyData[idx].replyContent,
            updatedAt: replyData[idx].updatedAt,
            inputStatus: status,
        }

        setReplyData([...replyData]);

        if(status)
            setModifyTextValue(replyData[idx].replyContent);
    }

    const handleModifyOnChange = (e) => {
        setModifyTextValue(e.target.value);
    }

    const handleModifySubmit = async (e) => {
        const idx = e.target.value;
        const replyId = replyData[idx].replyId;

        await axiosInstance.patch(`my-page/qna/product/reply`, {
            replyId: replyId
            , content: modifyTextValue,
        }, {
            headers: {'Content-Type': 'application/json'},
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getProductQnADetail();
            })
            .catch(err => {
                console.error('modifyReply Error : ', err);
            })
    }

    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    const handleInputSubmit = async () => {
        await axiosInstance.post(`my-page/qna/product/reply`, {
            qnaId: data.productQnAId,
            content: inputValue,
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getProductQnADetail();
            })
            .catch(err => {
                console.error('productQnADetail input submit error : ', err);
            })
    }

    const handleDeleteBtn = async () => {

        await axiosInstance.delete(`my-page/qna/product/${data.productQnAId}`)
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate('/my-page/qna/product')
            })
            .catch(err => {
                console.error('productQnA delete error : ', err);
            })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <QnADetail
                data={data}
                replyData={replyData}
                nickname={nickname}
                handleReplyModifyOpen={handleReplyModifyOpen}
                handleReplyModifyClose={handleReplyModifyClose}
                handleModifyOnChange={handleModifyOnChange}
                modifyTextValue={modifyTextValue}
                handleModifySubmit={handleModifySubmit}
                handleInputOnChange={handleInputOnChange}
                inputValue={inputValue}
                handleInputSubmit={handleInputSubmit}
                titleText={'상품 문의'}
                handleDeleteBtn={handleDeleteBtn}
            />
       </div>
    )
}

export default MyPageProductQnADetail;