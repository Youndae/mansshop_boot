import React, {useEffect} from 'react';
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {useState} from "@types/react";

function MemberQnA() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getMemberQnA();
    }, [page]);

    const getMemberQnA = async () => {

        await axiosInstance.get(`my-page/qna/member/${page}`)
            .then(res => {
                console.log('memberQnA res : ', res);

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

export default MemberQnA;