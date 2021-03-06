package com.sahudyscos.web.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import com.sahudyscos.web.controller.util.Pager;
import com.sahudyscos.web.entity.Album;
import com.sahudyscos.web.entity.Label;
import com.sahudyscos.web.entity.Release;
import com.sahudyscos.web.entity.key.ReleaseId;
import com.sahudyscos.web.repository.AlbumRepository;
import com.sahudyscos.web.repository.LabelRepository;
import com.sahudyscos.web.repository.ReleaseRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminReleaseController {
    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 10;
    private static final int[] PAGE_SIZES = {10, 20, 30};

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private LabelRepository labelRepository;


    @GetMapping("/admin/release")
    public String contract(Model model, @QuerydslPredicate(root = Release.class) Predicate predicate,
                           Pageable pageable, @RequestParam MultiValueMap<String, String> parameters, 
                           @RequestHeader(name = "Update-Table", defaultValue = "false") Boolean update) {
        model.addAttribute("releases", releaseRepository.findAll(predicate, pageable));
        model.addAttribute("release", new Release());
        return update ? "admin/release :: searchBody" : "admin/release";
    }

    @PostMapping(value = "/admin/release", consumes="application/json")
    @ResponseBody
    public Optional<Release> view (@RequestBody String json) {
        ObjectMapper mapper = new ObjectMapper();
        Optional<Release> currentRelease;
        ReleaseRequest request;
        try {
            request = mapper.readValue(json, ReleaseRequest.class);
            currentRelease = releaseRepository.findById(new ReleaseId (request.getId(), request.getAlbumId()));
            return currentRelease;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        }
        return null;
    }
    
    @GetMapping(value = "/admin/release/album")
    @ResponseBody
    @JsonIgnoreProperties({"description", "genre", "altGenre", "activityStart", "albums", "labelsContracted"})
	List<Album> getAlbum(@RequestParam String name) {
		return albumRepository.findByNameStartsWith(name);
    }

    @GetMapping(value = "/admin/release/label")
    @ResponseBody
    @JsonIgnoreProperties({"description", "genre", "altGenre", "activityStart", "phone", "altPhone", "artistsContracted", "releases"})
	List<Label> getLabels(@RequestParam String name) {
		return labelRepository.findByNameStartsWith(name);
    }

    @PostMapping("/admin/release/save")
    public ModelAndView create(@ModelAttribute Release release) {
        releaseRepository.save(release);
        return new ModelAndView("redirect:/admin/release");
    }

    @PostMapping("/admin/release/delete")
    public ModelAndView delete(@ModelAttribute Release release) {
        releaseRepository.delete(release);
        return new ModelAndView("redirect:admin/release");
    }
}

class ReleaseRequest {
    private String type;
    private Long albumId;
    private Long id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
