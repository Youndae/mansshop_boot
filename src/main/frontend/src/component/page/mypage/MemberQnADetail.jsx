import React, {useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";

function MemberQnADetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { qnaId } = useParams();

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getMemberQnADetail();
    }, [qnaId]);

    const getMemberQnADetail = async () => {

        await axiosInstance.get(`my-page/qna/member/detail/${qnaId}`)
            .then(res => {
                console.log('productQnADetail res : ', res);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('productQnA error : ', err);
            })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
        </div>
    )
}

export default MemberQnADetail;