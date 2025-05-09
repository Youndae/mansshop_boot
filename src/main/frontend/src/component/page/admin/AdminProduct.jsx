import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
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
        전체 상품 목록
        상품 추가 버튼, 상품 클릭 시 상품 정보 페이지로 이동

        상품명 기반 검색
     */
function AdminProduct() {
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

    const navigate = useNavigate();

    useEffect(() => {
        setKeywordInput(keyword == null ? '' : keyword);
        getProductList();
    }, [page, keyword]);

    //상품 목록 조회
    const getProductList = async () => {
        let url = `admin/product${createPageAndKeywordUrl(page, keyword)}`;

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
            })
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    //페이지네이션 이벤트 제어
    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            pageSubmit(pageNum, navigate);
        else
            searchPageSubmit(keyword, pageNum, navigate);
    }

    //검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    //검색 이벤트
    const handleSearchOnClick = async () => {
        searchSubmit(keywordInput, navigate);
    }

    //상품 추가 버튼 이벤트
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