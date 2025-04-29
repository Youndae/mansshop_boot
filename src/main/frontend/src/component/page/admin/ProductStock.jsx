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

import AdminSideNav from "../../ui/nav/AdminSideNav";
import Paging from "../../ui/Paging";

/*
        상품 재고 관리 페이지
        테이블 구조.
        상품 elements 클릭 시 상품 정보로 이동
        상품 elements 하위에
        상품 옵션별 elements를 통해 옵션별 재고 현황 확인 가능.

        검색은 상품명 기반.
     */
function ProductStock() {
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
        setKeywordInput(keyword);
        getProductStock();
    }, [page, keyword]);

    //상품 조회
    const getProductStock = async() => {
        let url = `admin/product/stock${createPageAndKeywordUrl(page, keyword)}`;

        await axiosInstance.get(url)
            .then(res => {
                console.log('stock res : ', res);

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


    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>상품 재고</h1>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table product-stock-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>상품명</th>
                                <th>총 재고</th>
                                <th>옵션 수</th>
                                <th>공개 여부</th>
                            </tr>
                        </thead>
                        <tbody>
                        {data.map((data, index) => {
                            return (
                                <StockBody
                                    key={index}
                                    data={data}
                                />
                            )
                        })}
                        </tbody>
                    </table>
                </div>
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
    )
}

function StockBody(props) {
    const { data } = props;

    let isOpenText = '비공개';
    if(data.isOpen)
        isOpenText = '공개';

    return (
        <>
            <tr>
                <td>{data.classification}</td>
                <td>
                    <Link to={`/admin/product/${data.productId}`}>
                        {data.productName}
                    </Link>
                </td>
                <td>{data.totalStock}</td>
                <td>{data.optionList.length}</td>
                <td>{isOpenText}</td>
            </tr>
            {data.optionList.map((optionData, index) => {
                const sizeText = `사이즈 : ${optionData.size}`;
                const colorText = `색상 : ${optionData.color}`;
                let optionText = '';
                if(optionData.size === null){
                    if(optionData.color !== null){
                        optionText = colorText;
                    }
                }else {
                    if(optionData.color !== null)
                        optionText = `${sizeText}, ${colorText}`;
                    else
                        optionText = sizeText;
                }
                let optionIsOpenText = '비공개';
                if(optionData.optionIsOpen)
                    optionIsOpenText = '공개';

                return (
                    <tr className="admin-stock-option">
                        <td colSpan={2}>{optionText}</td>
                        <td>{optionData.optionStock}</td>
                        <td></td>
                        <td>{optionIsOpenText}</td>
                    </tr>
                )
            })}
        </>

    )
}

export default ProductStock;