import React, {useState} from 'react';
import {Link, useNavigate} from "react-router-dom";

import "../css/header.css";


function Navbar() {
    const [keyword, setKeyword] = useState('');

    const navigate = useNavigate();

    const handleKeywordOnchange = (e) => {
        setKeyword(e.target.value);
    }

    const handleLoginBtn = () => {
        navigate('/login');
    }

    const handleCartBtn = () => {
        navigate('/cart');
    }

    const handleOrderBtn = () => {
        navigate('/order');
    }

    const handleSearchBtn = () => {
        //상품 검색 요청
        navigate(`/search?keyword=${keyword}`);
    }

    const handleClassificationBtn = (e) => {
        const btnName = e.target.textContent;
        console.log('navbar :: btnName : ', btnName);
        if(btnName === 'BEST')
            navigate('/');
        else if(btnName === 'NEW')
            navigate('/new');
        else
            navigate(`/category/${btnName}`);
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
                        <li>
                            <button className="header-btn" type={'button'} onClick={handleLoginBtn}>로그인</button>
                        </li>
                        <li>
                            <button className="header-btn" type={'button'} onClick={handleCartBtn}>장바구니</button>
                        </li>
                        <li>
                            <button className="header-btn" type={'button'} onClick={handleOrderBtn}>주문조회</button>
                        </li>
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

export default Navbar;