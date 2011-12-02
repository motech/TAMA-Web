package org.motechproject.tama.web.fileupload;

import org.apache.log4j.Logger;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.repository.AllIVRLanguages;
import org.motechproject.tama.tools.seed.MD5Checksum;
import org.motechproject.tama.web.view.IvrLanguagesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@RequestMapping("/upload")
@Controller
public class FileUploadController {

    Logger logger = Logger.getLogger(this.getClass());

    private final String FILE_UPLOAD_VIEW = "fileupload/select";
    private final String FILE_UPLOADED_VIEW = "fileupload/success";
    private CMSLiteService cmsLiteService;
    private AllIVRLanguages allIVRLanguages;

    @Autowired
    public FileUploadController(CMSLiteService cmsLiteService, AllIVRLanguages allIVRLanguages) {
        this.cmsLiteService = cmsLiteService;
        this.allIVRLanguages = allIVRLanguages;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String fileUploadView(Model uiModel) {
        FileUploadFormBean fileUploadFormBean = new FileUploadFormBean();
        fileUploadFormBean.setIvrLanguages(new IvrLanguagesView(allIVRLanguages).getAll());
        uiModel.addAttribute("model", fileUploadFormBean);
        return FILE_UPLOAD_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String uploadFile(FileUploadFormBean fileUploadFormBean, @RequestParam("attach") MultipartFile fileContent, Model uiModel) throws Exception, CMSLiteException {
        InputStream inputStream = fileContent.getInputStream();
        String tempFileName = System.getProperty("java.io.tmpdir") + fileContent.getOriginalFilename();
        File tempFile = new File(tempFileName);
        fileContent.transferTo(tempFile);
        String md5Checksum = new MD5Checksum().getMD5Checksum(tempFileName);
        cmsLiteService.addContent(new StreamContent(fileUploadFormBean.getLanguage(), fileContent.getOriginalFilename(), new FileInputStream(tempFileName), md5Checksum, "audio/x-wav"));
        logger.info("loaded " + fileContent.getName());
        tempFile.delete();
        inputStream.close();
        fileUploadFormBean.setFilename(fileContent.getOriginalFilename());
        uiModel.addAttribute("model", fileUploadFormBean);
        return FILE_UPLOADED_VIEW;
    }

}