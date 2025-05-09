import React, {useState} from 'react';
import {Link, useLocation, useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import { logout } from '../../../features/member/memberSlice';

import {axiosDefault, checkResponseMessageOk} from "../../../modules/customAxios";
import {handleLocationPathToLogin} from "../../../modules/loginModule";

import "../../css/header.css";

/*
    페이지 상단 Navbar
 */
function Navbar() {
    const status = useSelector((state) => state.member);
    const loginStatus = status.loginStatus;
    const adminStatus = status.role === 'admin';
    const { pathname } = useLocation();

    const [keyword, setKeyword] = useState('');

    const navigate = useNavigate();
    const dispatch = useDispatch();

    //검색 input 입력 이벤트
    const handleKeywordOnchange = (e) => {
        setKeyword(e.target.value);
    }

    //로그인 버튼 이벤트
    const handleLoginBtn = () => {
        handleLocationPathToLogin(pathname, navigate);
    }

    //로그아웃 버튼 이벤트
    const handleLogoutBtn = async () => {

        await axiosDefault.post(`member/logout`)
            .then(res => {
                if(checkResponseMessageOk(res)) {
                    window.localStorage.removeItem('Authorization');

                    dispatch(logout());
                    navigate('/');
                }else {
                    alert('오류가 발생했습니다.\n문제가 계속되면 관리자에게 문의해주세요');
                }
            })
            .catch(() => {
                alert('오류가 발생했습니다.\n문제가 계속되면 관리자에게 문의해주세요');
            })
    }

    //장바구니 버튼 이벤트
    const handleCartBtn = () => {
        navigate('/cart');
    }

    //주문 조회 버튼 이벤트
    const handleOrderBtn = () => {
        navigate('/order/info');
    }

    //마이페이지 버튼 이벤트
    const handleMyPageBtn = () => {
        navigate('/my-page/order');
    }

    //검색 버튼 이벤트
    const handleSearchBtn = () => {
        navigate(`/search?keyword=${keyword}`);
    }

    //상품 분류 버튼 이벤트
    const handleClassificationBtn = (e) => {
        const btnName = e.target.textContent;

        if(btnName === 'BEST')
            navigate('/');
        else if(btnName === 'NEW')
            navigate('/new');
        else
            navigate(`/category/${btnName}`);
    }

    //관리자 페이지 버튼 이벤트
    const handleAdminPageBtn = () => {
        navigate('/admin/product');
    }

    return (
        <div className="header">
            <div className="header-nav">
                <div className="header-nav logo">
                    <Link className="logo-link" to={'/'}>
                        <span>Man's Shop</span>
                    </Link>
                </div>
                <div className="header-nav menu">
                    <ul className="menu-nav">
                        <NavbarBtn
                            loginState={loginStatus}
                            adminStatus={adminStatus}
                            handleLoginBtn={handleLoginBtn}
                            handleLogoutBtn={handleLogoutBtn}
                            handleCartBtn={handleCartBtn}
                            handleOrderListBtn={handleOrderBtn}
                            handleMyPageBtn={handleMyPageBtn}
                            handleAdminPageBtn={handleAdminPageBtn}
                        />
                        <li>
                            <div className="main-search-form">
                                <input type={'text'} id={'keyword'} value={keyword} onChange={handleKeywordOnchange}/>
                                <div className="search-btn">
                                    <button className="search-button" type={'button'} onClick={handleSearchBtn}>
                                        <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"}/>
                                    </button>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
                <div className="header-nav top">
                    <button type={'button'} onClick={handleClassificationBtn}>BEST</button>
                    <button type={'button'} onClick={handleClassificationBtn}>NEW</button>
                    <button type={'button'} onClick={handleClassificationBtn}>OUTER</button>
                    <button type={'button'} onClick={handleClassificationBtn}>TOP</button>
                    <button type={'button'} onClick={handleClassificationBtn}>PANTS</button>
                    <button type={'button'} onClick={handleClassificationBtn}>SHOES</button>
                    <button type={'button'} onClick={handleClassificationBtn}>BAGS</button>
                </div>
            </div>
        </div>
    )
}

function NavbarBtn(props) {
    const { loginState, adminStatus, handleLoginBtn, handleLogoutBtn, handleCartBtn, handleOrderListBtn, handleMyPageBtn, handleAdminPageBtn} = props;

    if(loginState){

        if(adminStatus){
            return (
                <>
                    <li>
                        <button className="header-btn" type={'button'} onClick={handleLogoutBtn}>로그아웃</button>
                    </li>
                    <li>
                        <button className="header-btn" type={'button'} onClick={handleAdminPageBtn}>관리자페이지</button>
                    </li>
                    <li>
                        <button className="header-btn" type={'button'} onClick={handleCartBtn}>장바구니</button>
                    </li>
                </>
            )
        }

        return (
            <>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleLogoutBtn}>로그아웃</button>
                </li>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleMyPageBtn}>마이페이지</button>
                </li>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleCartBtn}>장바구니</button>
                </li>
            </>
        )
    }else {
        return (
            <>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleLoginBtn}>로그인</button>
                </li>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleOrderListBtn}>주문조회</button>
                </li>
                <li>
                    <button className="header-btn" type={'button'} onClick={handleCartBtn}>장바구니</button>
                </li>
            </>
        )
    }
}

export default Navbar;