package com.example.demo.security;

import com.example.demo.model.boards.Boards;
import com.example.demo.model.card_labels.Card_Labels;
import com.example.demo.model.cards.Card;
import com.example.demo.model.checklist.Checklist; // Checklist modelini import et
import com.example.demo.model.lists.Lists;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final MemberRepository memberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final RoleRepository roleRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ListsRepository listsRepository;
    private final CardRepository cardRepository;
    private final CardLabelsRepository cardLabelsRepository;
    private final ChecklistRepository checklistRepository; // Eksik olan repository'yi ekledik

    private static final String WORKSPACE_SCOPE = "WORKSPACE";
    private static final String BOARD_SCOPE = "BOARD";
    private static final String OWNER_ROLE = "OWNER";
    private static final String LEAD_ROLE = "LEAD";
    private static final String MEMBER_ROLE = "MEMBER";

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            log.warn("Kimlik doğrulama nesnesi yok veya türü UserDetails değil. Yetkilendirme reddedildi.");
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                log.info("Admin yetkisi tespit edildi. Tüm izinler kabul edildi.");
                return true;
            }
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<Member> memberOpt = memberRepository.findByEmail(email);

        if (memberOpt.isEmpty()) {
            log.warn("Yetkilendirme sırasında kullanıcı bulunamadı: {}", email);
            return false;
        }

        Member member = memberOpt.get();
        String permissionString = (String) permission;

        log.info("Yetkilendirme kontrolü başladı. Kullanıcı: {}, İzin: {}", email, permissionString);

        if (Boolean.TRUE.equals(member.getIsAdmin())) {
            log.info("Kullanıcı admin olduğu için yetkilendirme başarılı: {}", email);
            return true;
        }

        Integer targetId = null;
        if (targetDomainObject instanceof Integer) {
            targetId = (Integer) targetDomainObject;
        }

        if (targetId == null) {
            switch (permissionString) {
                case "create_workspace":
                    return true;
                case "read_all_workspaces":
                    return false;
                default:
                    log.warn("Hedef ID (targetId) null olduğu için yetkilendirme reddedildi. İzin: {}", permissionString);
                    return false;
            }
        }

        switch (permissionString) {
            case "read_workspaces_for_current_user":
                return true;
            case "read_workspaces_by_member":
                return member.getMemberId().equals(targetId);
            case "read_boards_in_workspace":
                log.info("Workspace içindeki board'ları okuma izni kontrol ediliyor. memberId: {}, workspaceId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndId(member.getMemberId(), WORKSPACE_SCOPE, targetId, OWNER_ROLE, MEMBER_ROLE, LEAD_ROLE);
            case "delete_workspace":
                log.info("Workspace silme izni kontrol ediliyor. memberId: {}, workspaceId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndId(member.getMemberId(), WORKSPACE_SCOPE, targetId, OWNER_ROLE);
            case "delete_list":
                log.info("Liste silme izni kontrol ediliyor. memberId: {}, listId: {}", member.getMemberId(), targetId);
                // Listeden boardId'yi al, sonra board üzerindeki rolü kontrol et
                return hasBoardMemberRole(member.getMemberId(), targetId, LEAD_ROLE);
            // ya da uygun gördüğün roller (örneğin sadece LEAD yetkili olabilir)
            case "read_board":
                log.info("Tekil board okuma izni kontrol ediliyor. memberId: {}, boardId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndBoardId(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "create_board":
            case "invite_member":
                log.info("Board oluşturma veya üye davet etme izni kontrol ediliyor. memberId: {}, workspaceId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndId(member.getMemberId(), WORKSPACE_SCOPE, targetId, OWNER_ROLE, LEAD_ROLE);
            case "read_workspace_members":
                log.info("Workspace üyelerini okuma izni kontrol ediliyor. memberId: {}, workspaceId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndId(member.getMemberId(), WORKSPACE_SCOPE, targetId, OWNER_ROLE);
            case "update_board":
            case "delete_board":
            case "read_board_members":
                log.info("Board üyelerini okuma izni kontrol ediliyor. memberId: {}, boardId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndBoardId(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);

            case "remove_member_from_board":
                log.info("Board düzenleme/silme izni kontrol ediliyor. memberId: {}, boardId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndBoardId(member.getMemberId(), targetId, LEAD_ROLE, OWNER_ROLE);
            case "promote_leader":
            case "add_member_to_board":
                log.info("Board üye yönetimi izni kontrol ediliyor. memberId: {}, boardId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndBoardId(member.getMemberId(), targetId, LEAD_ROLE, OWNER_ROLE);
            case "create_list":
                log.info("Liste oluşturma izni kontrol ediliyor. memberId: {}, boardId: {}", member.getMemberId(), targetId);
                return hasRoleByScopeAndBoardId(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "read_list":
                log.info("Liste okuma izni kontrol ediliyor. memberId: {}, listId: {}", member.getMemberId(), targetId);
                return hasBoardMemberRole(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "create_card":
                log.info("Kart oluşturma izni kontrol ediliyor. memberId: {}, listId: {}", member.getMemberId(), targetId);
                return hasBoardMemberRole(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "read_card":
            case "update_card":
            case "delete_card":
                log.info("Kart izni kontrol ediliyor (read/update/delete). memberId: {}, cardId: {}", member.getMemberId(), targetId);
                return hasCardPermission(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "create_card_label":
            case "delete_card_label":
                log.info("Kart etiketi yönetimi izni kontrol ediliyor. memberId: {}, cardId: {}", member.getMemberId(), targetId);
                return hasCardPermission(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "update_card_label":
                log.info("Kart etiketi güncelleme izni kontrol ediliyor. memberId: {}, cardLabelId: {}", member.getMemberId(), targetId);
                return hasCardLabelPermission(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "create_checklist":
                log.info("Checklist oluşturma izni kontrol ediliyor. memberId: {}, cardId: {}", member.getMemberId(), targetId);
                return hasCardPermission(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            case "create_checklist_item":
            case "update_checklist_item":
            case "delete_checklist_item":
                log.info("Checklist öğesi izni kontrol ediliyor. memberId: {}, checklistId: {}", member.getMemberId(), targetId);
                return hasChecklistPermission(member.getMemberId(), targetId, MEMBER_ROLE, LEAD_ROLE, OWNER_ROLE);
            default:
                log.warn("Bilinmeyen izin talebi: {} için yetkilendirme reddedildi.", permissionString);
                return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId == null) {
            return hasPermission(authentication, null, permission);
        }
        if (targetId instanceof Integer) {
            return hasPermission(authentication, (Integer) targetId, permission);
        }
        return false;
    }

    private boolean hasBoardMemberRole(Integer memberId, Integer listId, String... roleNames) {
        log.info("hasBoardMemberRole metodu çağrıldı. memberId: {}, listId: {}", memberId, listId);
        if (listId == null) {
            log.warn("hasBoardMemberRole metodu için listId null olamaz.");
            return false;
        }
        Optional<Lists> listOpt = listsRepository.findById(listId);
        if (listOpt.isPresent()) {
            Integer boardId = listOpt.get().getBoardId();
            return hasRoleByScopeAndBoardId(memberId, boardId, roleNames);
        }
        log.warn("Liste bulunamadı. listId: {}", listId);
        return false;
    }

    private boolean hasCardPermission(Integer memberId, Integer cardId, String... roleNames) {
        log.info("hasCardPermission metodu çağrıldı. memberId: {}, cardId: {}", memberId, cardId);
        if (cardId == null) {
            log.warn("hasCardPermission metodu için cardId null olamaz.");
            return false;
        }
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            Integer listId = cardOpt.get().getListId();
            return hasBoardMemberRole(memberId, listId, roleNames);
        }
        log.warn("Kart bulunamadı. cardId: {}", cardId);
        return false;
    }

    // ChecklistPermissionEvaluator'a eklenmesi gereken yeni helper method
    private boolean hasChecklistPermission(Integer memberId, Integer checklistId, String... roleNames) {
        log.info("hasChecklistPermission metodu çağrıldı. memberId: {}, checklistId: {}", memberId, checklistId);
        if (checklistId == null) {
            log.warn("hasChecklistPermission metodu için checklistId null olamaz.");
            return false;
        }
        Optional<Checklist> checklistOpt = checklistRepository.findById(checklistId);
        if (checklistOpt.isPresent()) {
            Integer cardId = checklistOpt.get().getCardId();
            return hasCardPermission(memberId, cardId, roleNames);
        }
        log.warn("Checklist bulunamadı. checklistId: {}", checklistId);
        return false;
    }

    private boolean hasCardLabelPermission(Integer memberId, Integer cardLabelId, String... roleNames) {
        log.info("hasCardLabelPermission metodu çağrıldı. memberId: {}, cardLabelId: {}", memberId, cardLabelId);
        if (cardLabelId == null) {
            log.warn("hasCardLabelPermission metodu için cardLabelId null olamaz.");
            return false;
        }
        Optional<Card_Labels> cardLabelOpt = cardLabelsRepository.findById(cardLabelId);
        if (cardLabelOpt.isPresent()) {
            Integer cardId = cardLabelOpt.get().getCardId();
            Optional<Card> cardOpt = cardRepository.findById(cardId);
            if (cardOpt.isPresent()) {
                Integer listId = cardOpt.get().getListId();
                return hasBoardMemberRole(memberId, listId, roleNames);
            }
        }
        log.warn("Kart etiketi veya bağlı kart bulunamadı. cardLabelId: {}", cardLabelId);
        return false;
    }

    private boolean hasRoleByScopeAndId(Integer memberId, String scope, Integer targetId, String... roleNames) {
        log.info("hasRoleByScopeAndId metodu çağrıldı. memberId: {}, scope: {}, targetId: {}", memberId, scope, targetId);
        if (targetId == null || !WORKSPACE_SCOPE.equalsIgnoreCase(scope)) {
            log.warn("Geçersiz çağrı. targetId null veya scope 'WORKSPACE' değil.");
            return false;
        }

        Optional<WorkspaceMember> membershipOpt = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndMember_MemberId(targetId, memberId);
        if (membershipOpt.isPresent() && membershipOpt.get().getRole() != null) {
            String userRoleName = membershipOpt.get().getRole().getRoleName();
            for (String name : roleNames) {
                if (name.equalsIgnoreCase(userRoleName)) {
                    log.info("Rol eşleşmesi başarılı. Kullanıcı yetkili. Rol: {}", userRoleName);
                    return true;
                }
            }
            log.warn("Rol eşleşmesi başarısız. Beklenen roller: {}, Kullanıcının rolü: {}", java.util.Arrays.toString(roleNames), userRoleName);
        } else {
            log.warn("Kullanıcı, belirtilen workspace'te bulunamadı veya rolü null. memberId: {}, workspaceId: {}", memberId, targetId);
        }
        return false;
    }

    private boolean hasRoleByScopeAndBoardId(Integer memberId, Integer boardId, String... roleNames) {
        log.info("hasRoleByScopeAndBoardId metodu çağrıldı. memberId: {}, boardId: {}", memberId, boardId);
        if (boardId == null) {
            log.warn("hasRoleByScopeAndBoardId metodu için boardId null olamaz.");
            return false;
        }

        Optional<Roles> roleOpt = boardMemberRepository.findRoleByBoardAndMember(boardId, memberId)
                .flatMap(roleId -> roleRepository.findById(roleId));

        if (roleOpt.isPresent() && BOARD_SCOPE.equalsIgnoreCase(roleOpt.get().getScope())) {
            String userRoleName = roleOpt.get().getRoleName();
            for (String name : roleNames) {
                if (name.equalsIgnoreCase(userRoleName)) {
                    log.info("Rol eşleşmesi başarılı. Kullanıcı yetkili. Rol: {}", userRoleName);
                    return true;
                }
            }
            log.warn("Rol eşleşmesi başarısız. Beklenen roller: {}, Kullanıcının rolü: {}", java.util.Arrays.toString(roleNames), userRoleName);
        } else {
            log.warn("Kullanıcı, belirtilen board'da bulunamadı veya rolü BOARD kapsamında değil. memberId: {}, boardId: {}", memberId, boardId);
        }
        return false;
    }
}
