import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit,
    searchPageSubmit, searchSubmit
} from "../../../modules/pagingModule";
import {numberComma} from "../../../modules/numberCommaModule";
import {createPageAndKeywordUrl} from "../../../modules/requestUrlModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import Paging from "../../ui/Paging";


/*
        상품별 매출.

        상품명 기반 검색 가능
 */
function AdminProductSales() {
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
        if(keyword !== null)
            setKeywordInput(keyword);

        getProductSales();
    }, [page, keyword]);

    //상품 매출 리스트 조회
    const getProductSales = async () => {
        let url = `admin/sales/product${createPageAndKeywordUrl(page, keyword)}`;

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

    //페이지네이션 제어
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

    //리스트 상품 Element 클릭 이벤트
    const handleProductOnClick = (productId) => {
        navigate(`/admin/sales/product/${productId}`);
    }


    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'sales'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>상품별 매출</h1>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table">
                        <thead>
                            <tr>
                                <th>상품 분류</th>
                                <th>상품명</th>
                                <th>매출</th>
                                <th>판매량</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((product, index) => {
                                return (
                                    <tr key={index} onClick={() => handleProductOnClick(product.productId)} className="tr-pointer">
                                        <td>{product.classification}</td>
                                        <td>{product.productName}</td>
                                        <td>{numberComma(product.sales)}</td>
                                        <td>{numberComma(product.salesQuantity)}</td>
                                    </tr>
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

export default AdminProductSales;