import React, {useEffect, useState} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    productDetailPagingObject, searchPageSubmit, searchSubmit
} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import Paging from "../../ui/Paging";
import {numberComma} from "../../../modules/numberCommaModule";

/*
        상품별 매출.

        옵션별이 아닌 상품별로 볼 수 있도록.
        분류, 상품명, 상품 총 매출, 판매량

        테이블 오른쪽 상단에서 분류 선택할 수 있도록 처리.
        조회 정렬은 분류 아이디 순서로.

        분류별 매출은 출력할 필요 없을 것 같고
        옵션에 대한 매출은 상세 페이지에서 확인하도록 처리.

        테이블 하단에는 상품명 검색과 페이징.

     */
function AdminProductSales() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
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
        if(keyword !== null)
            setKeywordInput(keyword);

        getProductSales();
    }, [page, keyword]);

    const getProductSales = async () => {
        let url = `admin/sales/product?page=${page}`;
        if(keyword !== null)
            url += `&keyword=${keyword}`;

        await axiosInstance.get(url)
            .then(res => {
                console.log('productSales res : ', res);
                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
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
        /*if(keyword == null)
            navigate(`/admin/product/discount?page=${pageNum}`);
        else
            navigate(`/admin/product/discount?keyword=${keyword}&page=${pageNum}`);*/
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleSearchOnClick = async () => {
        searchSubmit(keywordInput, navigate);
        // navigate(`/admin/product?keyword=${keywordInput}`);
    }

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