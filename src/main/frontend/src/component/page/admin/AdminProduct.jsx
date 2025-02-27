import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {createPageAndKeywordUrl} from "../../../modules/requestUrlModule";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit,
    searchPageSubmit, searchSubmit
} from "../../../modules/pagingModule";
import {numberComma} from "../../../modules/numberCommaModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import Paging from "../../ui/Paging";
import DefaultBtn from "../../ui/DefaultBtn";

import "../../css/admin.css";

/*
        상품 리스트를 출력하고
        상품 또는 옵션을 추가하고
        상품을 검색할 수도 있어야 하고
        카테고리 별로 상품을 볼 수도 있어야 한다.

        출력 정보로는 분류, 상품명, 재고, 옵션 수, 가격

        paging기능이 필요하다.

        디자인으로는

        테이블 상단에 상품 추가, 옵션 추가 버튼, 카테고리 select box를 배치하고
        테이블 하단에 검색과 페이징을 추가한다.
     */
function AdminProduct() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page');
    const keyword = params.get('keyword');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setKeywordInput(keyword == null ? '' : keyword);
        getProductList();
    }, [page, keyword]);

    const getProductList = async () => {
        let url = `admin/product${createPageAndKeywordUrl(page, keyword)}`;
        // url += createPageAndKeywordUrl(page, keyword);

        await axiosInstance.get(url)
            .then(res => {
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            pageSubmit(pageNum, navigate);
        else
            searchPageSubmit(keyword, pageNum, navigate);
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleSearchOnClick = async () => {
        searchSubmit(keywordInput, navigate);
    }

    const handleAddBtnOnClick = () => {
        navigate(`/admin/product/add`);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header admin-product-header">
                    <h1>상품 목록</h1>
                    <DefaultBtn
                        btnText={'상품 추가'}
                        onClick={handleAddBtnOnClick}
                    />
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>상품명</th>
                                <th>재고</th>
                                <th>옵션 수</th>
                                <th>가격</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((bodyData, index) => {
                                return (
                                    <AdminProductTableBody
                                        key={index}
                                        data={bodyData}
                                    />
                                )
                            })}
                        </tbody>
                    </table>
                    <div className="admin-search">
                        <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput}/>
                        <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchOnClick}/>
                        <Paging
                            pagingData={pagingData}
                            onClickNumber={handlePageBtn}
                            onClickPrev={handlePagePrev}
                            onClickNext={handlePageNext}
                            className={'like-paging'}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

function AdminProductTableBody(props) {
    const { data } = props;

    return (
        <tr>
            <td>{data.classification}</td>
            <td>
                <Link to={`/admin/product/${data.productId}`}>
                    {data.productName}
                </Link>
            </td>
            <td>{data.stock}</td>
            <td>{data.optionCount}</td>
            <td>{numberComma(data.price)}</td>
        </tr>
    )
}

export default AdminProduct;