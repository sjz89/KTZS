package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.NoticeRepository;
import me.daylight.ktzs.model.dto.NoticeDto;
import me.daylight.ktzs.model.entity.Notice;
import me.daylight.ktzs.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/10 23:57
 */
@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public Notice save(Notice notice) {
        return noticeRepository.saveAndFlush(notice);
    }

    @Override
    public NoticeDto findNoticeById(Long id) {
        return transformNotice(noticeRepository.findNoticesById(id).get(0));
    }

    @Override
    public List<NoticeDto> findNoticesByStudent(Long studentId) {
        List<Object[]> objects=noticeRepository.findNoticesByStudent(studentId);
        List<NoticeDto> noticeDtos=new ArrayList<>();
        for (Object[] object:objects)
            noticeDtos.add(transformNotice(object));
        return noticeDtos;
    }

    @Override
    public Notice getLatestByCourseId(Long id) {
        return noticeRepository.getLatestByCourseId(id);
    }

    private NoticeDto transformNotice(Object[] object){
        NoticeDto noticeDto=new NoticeDto();
        noticeDto.setId(((BigInteger)object[0]).longValue());
        noticeDto.setContent((String)object[1]);
        noticeDto.setTime(((Date)object[2]).getTime());
        noticeDto.setCourseName((String)object[3]);
        noticeDto.setTeacherName((String)object[4]);
        return noticeDto;
    }
}
