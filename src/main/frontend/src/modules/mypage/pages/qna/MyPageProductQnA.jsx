import React, {useEffect, useState} from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { getProductQnAList } from '../../services/mypageQnAService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';

import MyPageSideNav from '../../components/MyPageSideNav';
import Pagination from '../../../../common/components/Pagination';
import MyPageQnABody from '../../components/MyPageQnABody';

//상품 문의 목록
function MyPageProductQnA() {
	const [params] = useSearchParams();
	const { page } = Object.fromEntries(params);

	const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [qnaData, setQnAData] = useState([]);

    const navigate = useNavigate();

	useEffect(() => {
		window.scrollTo(0, 0);

		const getProductQnA = async() => {
			try {
				const res = await getProductQnAList(page);

				setQnAData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
			} catch (err) {
				console.log(err);
			}
		}

		getProductQnA();
	}, [page]);

	// 페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
		});
	}

	return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>상품 문의</h1>
                </div>
                <div className="mypage-qna-content">
                    <table className="qna-table">
                        <thead>
                            <tr>
                                <th>상품명</th>
                                <th>답변 상태</th>
                                <th>작성일</th>
                            </tr>
                        </thead>
                        <tbody>
                            {qnaData.map((data, index) => {
                                return (
									<MyPageQnABody
										key={index}
										type={'PRODUCT'}
										qnaClassification={''}
										qnaStatus={data.productQnAStat}
										qnaTitle={data.productName}
										qnaDate={data.createdAt}
										qnaId={data.productQnAId}
									/>
                                )
                            })}
                        </tbody>
                    </table>
                </div>
                <Pagination
                    pagingData={pagingData}
                    handlePageBtn={handlePageBtn}
                    className={'like-paging'}
                />
            </div>
        </div>
    )
	
}

export default MyPageProductQnA;
