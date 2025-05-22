import React, {useEffect, useState} from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { getMemberQnAList } from '../../services/mypageQnAService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';

import MyPageSideNav from '../../components/MyPageSideNav';
import Pagination from '../../../../common/components/Pagination';
import DefaultButton from '../../../../common/components/DefaultButton';
import MyPageQnABody from '../../components/MyPageQnABody';


/*
    회원 문의 목록
    회원 문의 작성은 여기에서 가능
 */
function MemberQnA() {
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

		const getMemberQnA = async() => {
			try {
				const res = await getMemberQnAList(page);

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

		getMemberQnA();
	}, [page]);
	
	// 페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
		});
	}

	// 문의 작성 페이지 이동 버튼 이벤트
	const handleInsertBtn = () => {
		navigate('/my-page/qna/member/write');
	}

	return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의 사항</h1>
                    <div className="mypage-qna-header-btn">
                        <DefaultButton onClick={handleInsertBtn} btnText={'문의하기'}/>
                    </div>
                </div>
                <div className="mypage-qna-content">
                    <table className="qna-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>제목</th>
                                <th>답변 상태</th>
                                <th>작성일</th>
                            </tr>
                        </thead>
                        <tbody>
                        {qnaData.map((data, index) => {
                            return (
								<MyPageQnABody
									key={index}
									type={'MEMBER'}
									qnaClassification={data.qnaClassification}
									qnaStatus={data.memberQnAStat}
									qnaTitle={data.memberQnATitle}
									qnaDate={data.updatedAt}
									qnaId={data.memberQnAId}
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

export default MemberQnA;