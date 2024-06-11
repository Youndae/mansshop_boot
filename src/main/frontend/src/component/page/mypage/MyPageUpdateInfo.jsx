import React, {useEffect} from "react";
import {useDispatch, useSelector} from "react-redux";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";

function MyPageUpdateInfo() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const dispatch = useDispatch();

    useEffect(() => {
        getUserInfo();
    }, []);

    const getUserInfo = async () => {

        await axiosInstance.get(`my-page/info`)
            .then(res => {
                console.log('myPage get info res : ', res);

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

export default MyPageUpdateInfo;