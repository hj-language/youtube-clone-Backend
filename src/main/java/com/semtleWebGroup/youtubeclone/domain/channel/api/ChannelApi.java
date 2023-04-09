package com.semtleWebGroup.youtubeclone.domain.channel.api;

import com.semtleWebGroup.youtubeclone.domain.channel.application.ChannelService;
import com.semtleWebGroup.youtubeclone.domain.channel.application.SubscribeService;
import com.semtleWebGroup.youtubeclone.domain.channel.domain.Channel;
import com.semtleWebGroup.youtubeclone.domain.channel.dto.ChannelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelApi {
    private final ChannelService channelService;
    private final SubscribeService subscribeService;

    /**
     * @param ChannelRequest form-data 형식으로 channelProfile.title, channelProfile.description, profile_img
     * @return 현재는 entity 자체를 반환. 프론트 사용 데이터 보고 overfetching 줄일 예정
     * @throws 이미지 첨부 관련 exception
     */
    @PostMapping("")
    public ResponseEntity create(@ModelAttribute ChannelRequest dto) throws Exception {
        Channel channel = channelService.addChannel(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    /**
     * @param channelId 구독할 채널, myid 로그인한 채널
     * @return 성공할 경우 OK, 실패할 경우 다른 ErrorResponse를 반환할 예정
     */
    @PostMapping("/{channelId}/subscribtion")
    public ResponseEntity subscribeChannel(@PathVariable("channelId")Long channelId,
                                           @RequestParam("myid")Long myid){
        subscribeService.subscribe(myid, channelId);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    /**
     * 채널 정보 조회
     * @param channelId
     * @return 현재는 entity 자체를 반환. 프론트 사용 데이터 보고 overfetching 줄일 예정
     */
    @GetMapping("/{channelId}")
    public ResponseEntity getChannelInfo(@PathVariable("channelId")Long channelId){
        Channel channel = channelService.getChannel(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    /**
     * @param channel 이름의 중복 제크
     * @return 중복이 아니면 OK, 중복이면 에러 코드 C001
     */
    @GetMapping("/validation")
    public ResponseEntity checkValidation(@RequestParam("name")String name){
        channelService.checkTitleValid(name);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    /**
     * @param myid 현재 로그인 한 채널의 정보. 토큰이나 세션으로 관리 필요
     * @return myid가 구독한 채널리스트 가져오기
     */
    @GetMapping("/subscribiton")
    public ResponseEntity getSubscribtionList(@RequestParam("myid")Long channelId){
        final Set<Channel> channelList = subscribeService.getSubscribedChannels(channelId);


        return ResponseEntity.status(HttpStatus.OK).body(channelList);
    }

    /**
     * 구독 취소
     * @param channelId
     * @return 성공시 OK 실패시 다른 ErrorCode 예정
     */
    @DeleteMapping("/{channelId}/subscribtion")
    public ResponseEntity cancleSubscribtion(@RequestParam("myid")Long myid,
                                             @PathVariable("channelId")Long channelId){
        subscribeService.unsubscribe(myid, channelId);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    /**
     * 채널 삭제
     * @param channelId
     * @return 성공시 OK 실패시 다른 ErrorCode 예정
     */
    @DeleteMapping("/{channelId}")
    public ResponseEntity deleteChannel(@PathVariable("channelId")Long channelId){
        channelService.deleteChannel(channelId);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    /**
     * @param channelId 수정할 channelId
     * @param ChannelRequest form-data 형식으로 channelProfile.title, channelProfile.description, profile_img
     * @return 현재는 entity 자체를 반환. 프론트 사용 데이터 보고 overfetching 줄일 예정
     * @throws Exception 이미지 첨부 관련 Exception
     */
    @PatchMapping("/{channelId}")
    public ResponseEntity editChannel(@PathVariable("channelId")Long channelId,
                                      @ModelAttribute ChannelRequest dto) throws Exception{
        final Channel channel = channelService.updateChannel(channelId, dto);


        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }
}
