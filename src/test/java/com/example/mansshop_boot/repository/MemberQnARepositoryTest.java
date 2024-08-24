package com.example.mansshop_boot.repository;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberQnARepositoryTest {

  /*  @Autowired
    private MemberQnARepository memberQnARepository;

    @Autowired
    private MemberQnAReplyRepository memberQnAReplyRepository;

    @Test
    @DisplayName("MemberQnAList 조회")
    void getList() {
        Pageable pageable = PageRequest.of(0
                                        , 20
                                        , Sort.by("createdAt").descending());
        String userId = "coco";

        Page<MemberQnAListDTO> dto = memberQnARepository.findAllByUserId(userId, pageable);

        String nickname = "코코에용";

        PagingResponseDTO<MemberQnAListDTO> response = new PagingResponseDTO<>(dto, new UserStatusDTO(nickname));

        System.out.println(response);
    }

    @Test
    @DisplayName("MemberQnADetail 조회. qnaId = 1522")
    void getDetail() {
        long memberQnAId = 1522L;

        MemberQnADTO qnaDTO = memberQnARepository.findByQnAId(memberQnAId);
        List<MyPageQnAReplyDTO> replyDTOList = memberQnAReplyRepository.findAllByQnAId(memberQnAId);

        String nickname = "테스터nick172";

        MemberQnADetailDTO response = new MemberQnADetailDTO(qnaDTO, replyDTOList);

        System.out.println(response);
    }*/
}