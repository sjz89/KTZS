package me.daylight.ktzs.service;

import me.daylight.ktzs.model.dto.NoticeDto;
import me.daylight.ktzs.model.entity.Notice;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/10 23:57
 */
public interface NoticeService {
    Notice save(Notice notice);

    NoticeDto findNoticeById(Long id);

    List<NoticeDto> findNoticesByStudent(Long studentId);

    Notice getLatestByCourseId(Long id);
}
