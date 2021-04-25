package com.example.demo.controller;

import com.example.demo.req.EbookReq;
import com.example.demo.resp.CommonResp;
import com.example.demo.resp.EbookResp;
import com.example.demo.resp.PageResp;
import com.example.demo.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req) {
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list= ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
