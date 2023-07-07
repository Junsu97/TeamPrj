package kopo.poly.service.impl;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.persistance.mapper.INoticeMapper;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService implements INoticeService {
    private final INoticeMapper noticeMapper;
    @Override
    public List<NoticeDTO> getNoticeList() throws Exception {
        log.info(this.getClass().getName()+".getNoticeList Start!");
        return noticeMapper.getNoticeList();
    }

    @Transactional
    @Override
    public NoticeDTO getNoticeInfo(NoticeDTO pDTO, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".getNoticeInfo Start!");

        // 상세보기할 때마다, 조회수 증가하기(수정보기는 제외)
        if(type){
            log.info("Update ReadCNT");
            noticeMapper.updateNoticeReadCnt(pDTO);
        }

        return noticeMapper.getNoticeInfo(pDTO);
    }
    @Transactional
    @Override
    public void insertNoticeInfo(NoticeDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertNoticeInfo Start!");

        noticeMapper.insertNoticeInfo(pDTO);
    }

    @Override
    public void updateNoticeInfo(NoticeDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updateNoticeInfo");

        noticeMapper.updateNoticeInfo(pDTO);
    }

    @Override
    public void deleteNoticeInfo(NoticeDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deleteNoticeInfo Start!");

        Optional<NoticeDTO> res = Optional.ofNullable(noticeMapper.getNoticeInfo(pDTO));

        if(res.isPresent()){
            noticeMapper.deleteNoticeInfo(pDTO);
            log.info("게시글이 삭제되었습니다.");
        }else{
            log.info("해당 게시글이 존재하지 않습니다.");
        }
    }
}
